package com.chatia.application;

import com.chatia.application.output.TransactionOutput;
import com.chatia.domain.TransactionId;
import com.chatia.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListAllTransactionsUseCase {
    private final TransactionRepository transactionRepository;

    public ListAllTransactionsUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "list-all-transactions", description = "Lista todas as transações financeiras registradas")
    public List<TransactionOutput> execute() {
        return transactionRepository.findAll().stream().map(TransactionOutput::from).toList();
    }
}
