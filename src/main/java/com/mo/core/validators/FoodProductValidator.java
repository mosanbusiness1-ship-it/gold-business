package com.mo.core.validators;

import org.springframework.stereotype.Component;

import com.mo.core.model.products.FoodProduct;

@Component 
public class FoodProductValidator implements BusinessValidator<FoodProduct> {

    @Override
    public void validate(FoodProduct product) {
        if (product.getExpiryDate() == null) {
            throw new IllegalArgumentException("La date d'expiration est obligatoire.");
        }
        // if (product.getOriginCountry() == null || product.getOriginCountry().isBlank()) {
        //     throw new IllegalArgumentException("Le pays d'origine est requis.");
        // }
        if (product.getCategory() == null) {
            throw new IllegalArgumentException("La catégorie alimentaire est requise.");
        }
    }
}

