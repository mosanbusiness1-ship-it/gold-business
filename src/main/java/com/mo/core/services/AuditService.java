package com.mo.core.services;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.mo.core.model.products.ProductHistory;
import com.mo.core.repositories.jpa.ProductHistoryRepository;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class AuditService {

    private final ProductHistoryRepository historyRepository;

    public AuditService(ProductHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public void logHistory(Long productId, String action, int oldVersion, int newVersion) {
        ProductHistory history = new ProductHistory();
        history.setProductId(productId);
        history.setAction(action);
        history.setUsername(getCurrentUsername());
        history.setTimestamp(LocalDateTime.now());
        history.setOldVersion(oldVersion);
        history.setNewVersion(newVersion);

        historyRepository.save(history);
    }

    public List<ProductHistory> getProductHistory(Long productId) {
        return historyRepository.findByProductIdOrderByTimestampDesc(productId);
    }

    private String getCurrentUsername() {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            return authentication != null ? authentication.getName() : "Système";
        } catch (Exception e) {
            return "Système";
        }
    }
}

