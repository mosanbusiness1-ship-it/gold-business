package com.mo.core.services.pricing;

import org.springframework.stereotype.Service;

import com.mo.core.model.products.AbstractProduct;
import com.mo.core.services.PricingService;

import java.math.BigDecimal;

@Service
public class BasicPricingService implements PricingService {
    @Override
    public BigDecimal calculatePrice(AbstractProduct product) {
        return product.getPrice();
    }

    @Override
    public BigDecimal applyDiscount(AbstractProduct product, BigDecimal discount) {
        return product.getPrice().multiply(BigDecimal.ONE.subtract(discount));
    }
}
