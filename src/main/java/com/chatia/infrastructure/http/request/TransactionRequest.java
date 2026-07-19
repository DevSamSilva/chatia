package com.chatia.infrastructure.http.request;

import com.chatia.application.input.PersistTransactionInput;
import com.chatia.domain.Category;

public record TransactionRequest(String description, Category category, long amount) {
    public PersistTransactionInput toInput() {
        return new PersistTransactionInput(description, amount, category);
    }
}
