package com.chatia.ai;

import com.chatia.application.GetTransactionSummaryUseCase;
import com.chatia.application.ListAllTransactionsUseCase;
import com.chatia.application.ListTransactionsByCategoryUseCase;
import com.chatia.application.PersistTransactionUseCase;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;

@Service
public class AiChatService {
    private final ChatClient chatClient;

    public AiChatService(PersistTransactionUseCase persistTransactionUseCase,
                         ListTransactionsByCategoryUseCase listTransactionsByCategoryUseCase,
                         ListAllTransactionsUseCase listAllTransactionsUseCase,
                         GetTransactionSummaryUseCase getTransactionSummaryUseCase,
                         ChatClient.Builder chatClientBuilder,
                         @Value("classpath:prompts/system-message.st") Resource systemPrompt) throws IOException {
        this.chatClient = chatClientBuilder
                .defaultSystem(systemPrompt.getContentAsString(Charset.defaultCharset()))
                .defaultTools(persistTransactionUseCase, listTransactionsByCategoryUseCase,
                        listAllTransactionsUseCase, getTransactionSummaryUseCase)
                .build();
    }

    public String chat(String message) {
        return chatClient.prompt().user(message).call().content();
    }
}
