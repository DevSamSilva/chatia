package com.chatia.infrastructure.persistence.entity;

import com.chatia.domain.Category;
import com.chatia.domain.Transaction;
import com.chatia.domain.TransactionId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class TransactionEntity {
    @Id
    private UUID id;
    private String description;
    private long amount;

    @Enumerated(EnumType.STRING)
    private Category category;
    private LocalDateTime createdAt;

    public TransactionEntity() {}

    public TransactionEntity(UUID id, String description, long amount, Category category, LocalDateTime createdAt) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.createdAt = createdAt;
    }

    public static TransactionEntity from(Transaction transaction) {
        return new TransactionEntity(
                transaction.getId().uuid(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getCategory(),
                transaction.getCreatedAt());
    }

    public Transaction toDomain() {
        return new Transaction(
                new TransactionId(this.id),
                this.description,
                this.amount,
                this.category,
                this.createdAt
        );
    }

    public UUID getId() { return id; }
    public String getDescription() { return description; }
    public long getAmount() { return amount; }
    public Category getCategory() { return category; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(UUID id) { this.id = id; }
    public void setDescription(String description) { this.description = description; }
    public void setAmount(long amount) { this.amount = amount; }
    public void setCategory(Category category) { this.category = category; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
