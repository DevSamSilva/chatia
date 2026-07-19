package com.chatia.infrastructure.persistence.repository;

import com.chatia.domain.Category;
import com.chatia.domain.Transaction;
import com.chatia.domain.TransactionId;
import com.chatia.domain.TransactionRepository;
import com.chatia.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaTransactionRepository implements TransactionRepository {
    private final TransactionEntityRepository transactionEntityRepository;

    public JpaTransactionRepository(TransactionEntityRepository transactionEntityRepository) {
        this.transactionEntityRepository = transactionEntityRepository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        var entity = TransactionEntity.from(transaction);
        return transactionEntityRepository.save(entity).toDomain();
    }

    @Override
    public Optional<Transaction> findById(TransactionId id) {
        return transactionEntityRepository.findById(id.uuid()).map(TransactionEntity::toDomain);
    }

    @Override
    public List<Transaction> findAll() {
        return ((List<TransactionEntity>) transactionEntityRepository.findAll())
                .stream()
                .map(TransactionEntity::toDomain)
                .toList();
    }

    @Override
    public List<Transaction> findAllByCategory(Category category) {
        return transactionEntityRepository.findAllByCategory(category)
                .stream()
                .map(TransactionEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteById(TransactionId id) {
        transactionEntityRepository.deleteById(id.uuid());
    }
}
