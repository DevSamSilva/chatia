package com.chatia.infrastructure.http.response;

import com.chatia.application.output.TransactionOutput;

public record TransactionResponse(String id, String category, String description, double amount, String createdAt) {
    public static TransactionResponse from(TransactionOutput output) {
        return new TransactionResponse(output.id(), output.category(), output.description(), output.value(), output.createdAt());
    }
}
