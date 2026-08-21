package com.mo.core.repositories.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mo.core.model.transactions.ProductTransaction;


@Repository
public interface ProductTransactionRepository extends JpaRepository<ProductTransaction, Long> {
    List<ProductTransaction> findByBuyerId(Long buyerId);
    List<ProductTransaction> findBySellerId(Long sellerId);
}
