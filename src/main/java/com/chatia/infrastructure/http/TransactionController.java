package com.chatia.infrastructure.http;

import com.chatia.application.*;
import com.chatia.application.output.SummaryOutput;
import com.chatia.application.output.TransactionOutput;
import com.chatia.domain.Category;
import com.chatia.domain.TransactionId;
import com.chatia.domain.TransactionRepository;
import com.chatia.infrastructure.http.request.TransactionRequest;
import com.chatia.infrastructure.http.response.SummaryResponse;
import com.chatia.infrastructure.http.response.TransactionResponse;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final PersistTransactionUseCase persistTransactionUseCase;
    private final ListTransactionsByCategoryUseCase listTransactionsByCategoryUseCase;
    private final ListAllTransactionsUseCase listAllTransactionsUseCase;
    private final GetTransactionSummaryUseCase getTransactionSummaryUseCase;
    private final TransactionRepository transactionRepository;

    private final TranscriptionModel transcriptionModel;
    private final ChatClient chatClient;
    private final TextToSpeechModel textToSpeechModel;

    public TransactionController(PersistTransactionUseCase persistTransactionUseCase,
                                 ListTransactionsByCategoryUseCase listTransactionsByCategoryUseCase,
                                 ListAllTransactionsUseCase listAllTransactionsUseCase,
                                 GetTransactionSummaryUseCase getTransactionSummaryUseCase,
                                 TransactionRepository transactionRepository,
                                 TranscriptionModel transcriptionModel,
                                 @Value("classpath:prompts/system-message.st") Resource systemPrompt,
                                 ChatClient.Builder chatClientBuilder,
                                 TextToSpeechModel textToSpeechModel) throws IOException {
        this.persistTransactionUseCase = persistTransactionUseCase;
        this.listTransactionsByCategoryUseCase = listTransactionsByCategoryUseCase;
        this.listAllTransactionsUseCase = listAllTransactionsUseCase;
        this.getTransactionSummaryUseCase = getTransactionSummaryUseCase;
        this.transactionRepository = transactionRepository;
        this.transcriptionModel = transcriptionModel;
        this.chatClient = chatClientBuilder
                .defaultSystem(systemPrompt.getContentAsString(Charset.defaultCharset()))
                .defaultTools(persistTransactionUseCase, listTransactionsByCategoryUseCase,
                        listAllTransactionsUseCase, getTransactionSummaryUseCase)
                .build();
        this.textToSpeechModel = textToSpeechModel;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(@RequestBody TransactionRequest request) {
        var transaction = persistTransactionUseCase.execute(request.toInput());
        return TransactionResponse.from(transaction);
    }

    @GetMapping
    public List<TransactionResponse> listAllTransactions() {
        return listAllTransactionsUseCase.execute().stream().map(TransactionResponse::from).toList();
    }

    @GetMapping("/{category}")
    public List<TransactionResponse> readTransactions(@PathVariable Category category) {
        return listTransactionsByCategoryUseCase.execute(category).stream().map(TransactionResponse::from).toList();
    }

    @GetMapping("/summary")
    public List<SummaryResponse> getSummary() {
        return getTransactionSummaryUseCase.execute().stream().map(SummaryResponse::from).toList();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable String id) {
        var transactionId = new TransactionId(java.util.UUID.fromString(id));
        var transaction = transactionRepository.findById(transactionId);
        if (transaction.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        transactionRepository.deleteById(transactionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/ai", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "audio/mp3")
    ResponseEntity<Resource> transcribe(@RequestParam("file") MultipartFile file) {
        var userMessage = transcriptionModel.transcribe(file.getResource());
        var result = chatClient.prompt().user(userMessage).call().content();

        byte[] audio = textToSpeechModel.call(result);
        var resource = new ByteArrayResource(audio);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("audio.mp3")
                                .build()
                                .toString())
                .body(resource);
    }
}
