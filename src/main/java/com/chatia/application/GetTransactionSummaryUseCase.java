package com.chatia.application;

import com.chatia.application.output.SummaryOutput;
import com.chatia.domain.Category;
import com.chatia.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GetTransactionSummaryUseCase {
    private final TransactionRepository transactionRepository;

    public GetTransactionSummaryUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "get-transaction-summary", description = "Retorna um resumo dos gastos totais por categoria")
    public List<SummaryOutput> execute() {
        var summaries = new ArrayList<SummaryOutput>();
        for (Category category : Category.values()) {
            var transactions = transactionRepository.findAllByCategory(category);
            if (!transactions.isEmpty()) {
                double total = transactions.stream().mapToLong(t -> t.getAmount()).sum() / 100.0;
                summaries.add(new SummaryOutput(category.name(), transactions.size(), total));
            }
        }
        return summaries;
    }

    @Tool(name = "get-total-spent", description = "Retorna o valor total gasto em uma categoria específica")
    public SummaryOutput executeByCategory(
            @ToolParam(description = "Categoria para consultar o total gasto") Category category) {
        var transactions = transactionRepository.findAllByCategory(category);
        double total = transactions.stream().mapToLong(t -> t.getAmount()).sum() / 100.0;
        return new SummaryOutput(category.name(), transactions.size(), total);
    }
}
