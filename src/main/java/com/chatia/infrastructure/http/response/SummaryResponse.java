package com.chatia.infrastructure.http.response;

import com.chatia.application.output.SummaryOutput;

public record SummaryResponse(String category, int totalTransactions, double totalAmount) {
    public static SummaryResponse from(SummaryOutput output) {
        return new SummaryResponse(output.category(), output.totalTransactions(), output.totalAmount());
    }
}
