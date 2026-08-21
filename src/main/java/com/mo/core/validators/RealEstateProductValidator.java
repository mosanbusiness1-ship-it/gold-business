package com.mo.core.validators;

import org.springframework.stereotype.Component;

import com.mo.core.model.products.RealEstateProduct;

@Component 
public class RealEstateProductValidator implements BusinessValidator<RealEstateProduct> {

    @Override
    public void validate(RealEstateProduct product) {
        if (Boolean.FALSE.equals(product.getIsForSale()) && Boolean.FALSE.equals(product.getIsForRent())) {
            throw new IllegalArgumentException("Le bien doit être à vendre ou à louer.");
        }

        if (product.getSurfaceArea() == null || product.getSurfaceArea() <= 0) {
            throw new IllegalArgumentException("La surface doit être positive.");
        }
    }
}

