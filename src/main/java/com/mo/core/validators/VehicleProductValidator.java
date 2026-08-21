package com.mo.core.validators;

import org.springframework.stereotype.Component;

import com.mo.core.model.products.VehicleProduct;


@Component 
public class VehicleProductValidator implements BusinessValidator<VehicleProduct> {

    @Override
    public void validate(VehicleProduct product) {
        if (product.getMake() == null || product.getMake().isBlank()) {
            throw new IllegalArgumentException("Le constructeur est obligatoire.");
        }
        if (product.getModel() == null || product.getModel().isBlank()) {
            throw new IllegalArgumentException("Le modèle est obligatoire.");
        }
        if (product.getManufacturingYear() == null || product.getManufacturingYear() < 1900) {
            throw new IllegalArgumentException("L'année de fabrication doit être valide.");
        }
        if (product.getVehicleType() == null) {
            throw new IllegalArgumentException("Le type de véhicule est requis.");
        }
    }
}

