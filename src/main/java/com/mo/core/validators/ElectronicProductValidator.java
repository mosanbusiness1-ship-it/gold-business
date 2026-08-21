package com.mo.core.validators;

import org.springframework.stereotype.Component;

import com.mo.core.model.products.ElectronicProduct;

@Component 
public class ElectronicProductValidator implements BusinessValidator<ElectronicProduct> {

    @Override
    public void validate(ElectronicProduct product) {
        if (product.getBrand() == null || product.getBrand().isBlank()) {
            throw new IllegalArgumentException("La marque est obligatoire pour un produit électronique.");
        }
        if (product.getModel() == null || product.getModel().isBlank()) {
            throw new IllegalArgumentException("Le modèle est obligatoire pour un produit électronique.");
        }
        if (product.getElectronicType() == null) {
            throw new IllegalArgumentException("Le type électronique est obligatoire.");
        }
    }
}

