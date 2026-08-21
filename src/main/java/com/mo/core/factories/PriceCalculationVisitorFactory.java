package com.mo.core.factories;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.mo.core.visitors.product_visitors.PriceCalculationVisitor;

@Component
public class PriceCalculationVisitorFactory {

    public PriceCalculationVisitor create(BigDecimal discountRate,
                                          BigDecimal taxRate,
                                          boolean applyOrganicPremium,
                                          boolean applySeasonalDiscount) {
        return new PriceCalculationVisitor(discountRate, taxRate, applyOrganicPremium, applySeasonalDiscount);
    }
}


