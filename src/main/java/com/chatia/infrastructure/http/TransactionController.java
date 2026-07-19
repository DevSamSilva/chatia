package com.chatia.infrastructure.http;

import com.chatia.application.*;
import com.chatia.application.output.TransactionOutput;
import com.chatia.domain.Category;
import com.chatia.domain.TransactionId;
import com.chatia.domain.TransactionRepository;
import com.chatia.infrastructure.http.request.TransactionRequest;
import com.chatia.infrastructure.http.response.TransactionResponse;
import com.chatia.ai.AiChatService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final PersistTransactionUseCase persistTransactionUseCase;
    private final ListTransactionsByCategoryUseCase listTransactionsByCategoryUseCase;
    private final ListAllTransactionsUseCase listAllTransactionsUseCase;
    private final TransactionRepository transactionRepository;
    private final AiChatService aiChatService;

    public TransactionController(PersistTransactionUseCase persistTransactionUseCase,
                                 ListTransactionsByCategoryUseCase listTransactionsByCategoryUseCase,
                                 ListAllTransactionsUseCase listAllTransactionsUseCase,
                                 TransactionRepository transactionRepository,
                                 AiChatService aiChatService) {
        this.persistTransactionUseCase = persistTransactionUseCase;
        this.listTransactionsByCategoryUseCase = listTransactionsByCategoryUseCase;
        this.listAllTransactionsUseCase = listAllTransactionsUseCase;
        this.transactionRepository = transactionRepository;
        this.aiChatService = aiChatService;
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

    @PostMapping("/ai")
    public ResponseEntity<String> chat(@RequestBody String message) {
        var result = aiChatService.chat(message);
        return ResponseEntity.ok(result);
    }
}
