package com.mo.core.visitors.product_visitors;

import com.mo.core.enums.FashionType;
import com.mo.core.model.products.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;


public class PriceCalculationVisitor implements ProductVisitor<BigDecimal> {

    private final BigDecimal discountRate;
    private final BigDecimal taxRate;
    private final boolean applyOrganicPremium;
    private final boolean applySeasonalDiscount;

    public PriceCalculationVisitor(BigDecimal discountRate, BigDecimal taxRate, boolean applyOrganicPremium, boolean applySeasonalDiscount) {
        this.discountRate = discountRate;
        this.taxRate = taxRate;
		this.applyOrganicPremium = applyOrganicPremium;
		this.applySeasonalDiscount = applySeasonalDiscount;
		
    }

    @Override
    public BigDecimal visit(ServiceProduct product) {
        Long durationInSeconds = product.getDuration();
        if (durationInSeconds == null) {
            // Durée inconnue, gérer selon ta logique (ex: retourner prix sans durée, ou 0)
            return BigDecimal.ZERO;
        }
        // Convertir secondes en heures (double pour précision)
        double durationInHours = durationInSeconds / 3600.0;

        BigDecimal basePrice = product.getPrice()
            .multiply(BigDecimal.valueOf(durationInHours));

        return applyTaxes(applyDiscount(basePrice));
    }

    
    @Override
    public BigDecimal visit(VehicleProduct product) {
        BigDecimal basePrice = product.getPrice();
        // Older vehicles get bigger discount
        if (product.getManufacturingYear() < LocalDate.now().getYear() - 5) {
            basePrice = basePrice.multiply(BigDecimal.valueOf(0.85)); // 15% discount
        }
        return applyTaxes(applyDiscount(basePrice));
    }

    @Override
    public BigDecimal visit(ElectronicProduct product) {
        BigDecimal basePrice = product.getPrice();
        // Special discount for older models
        if (product.getModel().contains("2022")) {
            basePrice = basePrice.multiply(BigDecimal.valueOf(0.80)); // 20% discount
        }
        return applyTaxes(applyDiscount(basePrice));
    }
    
    @Override
    public BigDecimal visit(FoodProduct product) {
        BigDecimal price = product.getPrice();
        
        // Organic premium
        if (applyOrganicPremium && product.getOrganic()) {
            price = price.multiply(BigDecimal.valueOf(1.15)); // 15% premium
        }
        
        // Discount for near-expiry items
        if (product.getExpiryDate() != null && 
            product.getExpiryDate().isBefore(LocalDate.now().plusDays(7))) {
            price = price.multiply(BigDecimal.valueOf(0.70)); // 30% discount
        }
        
        return applyTaxes(applyDiscount(price));
    }

    @Override
    public BigDecimal visit(FashionProduct product) {
        BigDecimal price = product.getPrice();
        
        // Seasonal discount for fashion
        if (applySeasonalDiscount) {
            price = price.multiply(BigDecimal.valueOf(0.80)); // 20% off
        }
        
        // Premium for jewelry
        if (product.getFashionType() == FashionType.JEWELRY) {
            price = price.multiply(BigDecimal.valueOf(1.10)); // 10% premium
        }
        
        return applyTaxes(applyDiscount(price));
    }
    

    @Override
    public BigDecimal visit(RealEstateProduct product) {
        BigDecimal price = product.getPrice()
            .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP); // Monthly
        return applyTaxes(applyDiscount(price));
    }

    private BigDecimal applyDiscount(BigDecimal price) {
        return price.multiply(BigDecimal.ONE.subtract(discountRate));
    }

    private BigDecimal applyTaxes(BigDecimal price) {
        return price.multiply(BigDecimal.ONE.add(taxRate));
    }
}
