package com.mo.core.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mo.core.model.products.ProductHistory;

import java.util.List;

@Repository
public interface ProductHistoryRepository extends JpaRepository<ProductHistory, Long> {

    // Pour récupérer l'historique d'un produit donné
    List<ProductHistory> findByProductIdOrderByTimestampDesc(Long productId);
}

