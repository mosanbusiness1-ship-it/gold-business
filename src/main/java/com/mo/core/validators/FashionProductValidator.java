package com.mo.core.validators;

import org.springframework.stereotype.Component;

import com.mo.core.model.products.FashionProduct;

@Component 
public class FashionProductValidator implements BusinessValidator<FashionProduct> {

    @Override
    public void validate(FashionProduct product) {
        if (product.getFashionType() == null) {
            throw new IllegalArgumentException("Le type de mode est requis.");
        }
        if (product.getSize() == null || product.getSize().isBlank()) {
            throw new IllegalArgumentException("La taille est requise.");
        }
        if (product.getBrand() == null || product.getBrand().isBlank()) {
            throw new IllegalArgumentException("La marque est requise.");
        }
        if (product.getTargetGender() == null || product.getTargetGender().isBlank()) {
            throw new IllegalArgumentException("Le genre ciblé est requis.");
        }
    }
}

