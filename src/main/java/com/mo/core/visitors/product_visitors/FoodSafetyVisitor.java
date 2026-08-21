package com.mo.core.visitors.product_visitors;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

import com.mo.core.enums.FoodCategory;
import com.mo.core.model.products.ElectronicProduct;
import com.mo.core.model.products.FashionProduct;
import com.mo.core.model.products.FoodProduct;
import com.mo.core.visitors.Visitor;
import com.mo.core.model.products.RealEstateProduct;
import com.mo.core.model.products.ServiceProduct;
import com.mo.core.model.products.VehicleProduct;

import lombok.AllArgsConstructor;
import lombok.Data;

@Visitor("foodSafety")
@Component
public class FoodSafetyVisitor implements ProductVisitor<com.mo.core.visitors.product_visitors.FoodSafetyVisitor.FoodSafetyReport> {
    
    // Implémentation spécifique pour FoodProduct
    @Override
    public FoodSafetyReport visit(FoodProduct product) {
        if (product.getExpiryDate() == null) {
            return new FoodSafetyReport(
                product.getId(),
                false,
                "MISSING_EXPIRY_DATE",
                RiskLevel.HIGH
            );
        }

        boolean isSafe = checkExpiry(product.getExpiryDate());
        RiskLevel riskLevel = calculateRiskLevel(product);
        
        return new FoodSafetyReport(
            product.getId(),
            isSafe,
            isSafe ? null : "EXPIRED",
            riskLevel
        );
    }

    // Implémentations par défaut pour les autres types
  
    @Override
    public FoodSafetyReport visit(ServiceProduct product) {
        return FoodSafetyReport.NOT_APPLICABLE;
    }

    @Override
    public FoodSafetyReport visit(RealEstateProduct product) {
        return FoodSafetyReport.NOT_APPLICABLE;
    }

    @Override
    public FoodSafetyReport visit(VehicleProduct product) {
        return FoodSafetyReport.NOT_APPLICABLE;
    }

    @Override
    public FoodSafetyReport visit(ElectronicProduct product) {
        return FoodSafetyReport.NOT_APPLICABLE;
    }

    @Override
    public FoodSafetyReport visit(FashionProduct product) {
        return FoodSafetyReport.NOT_APPLICABLE;
    }

    // Méthodes utilitaires
    private boolean checkExpiry(LocalDate expiryDate) {
        return expiryDate.isAfter(LocalDate.now());
    }

    private RiskLevel calculateRiskLevel(FoodProduct product) {
        if (product.getCategory() == FoodCategory.DAIRY || product.getCategory() == FoodCategory.MEAT) {
            return product.getExpiryDate().isBefore(LocalDate.now().plusDays(3)) ? 
                   RiskLevel.HIGH : RiskLevel.MEDIUM;
        }
        return RiskLevel.LOW;
    }

    // Enums et Classes internes
    public enum RiskLevel {
        LOW, MEDIUM, HIGH
    }

    @Data
    @AllArgsConstructor
    public static class FoodSafetyReport {
        public static final FoodSafetyReport NOT_APPLICABLE = 
            new FoodSafetyReport(null, true, "NOT_APPLICABLE", null);

        private Long productId;
        private boolean isSafe;
        private String warning;
        private RiskLevel riskLevel;
    }
}
