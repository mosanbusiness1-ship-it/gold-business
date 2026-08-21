package com.mo.core.services;

import java.math.BigDecimal;

import com.mo.core.model.products.AbstractProduct;

public interface PricingService {
    BigDecimal calculatePrice(AbstractProduct product);
    BigDecimal applyDiscount(AbstractProduct product, BigDecimal discount);
}