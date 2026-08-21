package com.mo.core.validators;

import org.springframework.stereotype.Component;

import com.mo.core.model.products.ServiceProduct;

@Component 
public class ServiceProductValidator implements BusinessValidator<ServiceProduct> {

    @Override
    public void validate(ServiceProduct product) {
        if (product.getDuration() == null || product.getDuration() == 0L) {
            throw new IllegalArgumentException("La durée du service est obligatoire.");
        }

        if (product.getServiceProvider() == null || product.getServiceProvider().isBlank()) {
            throw new IllegalArgumentException("Le prestataire de service est requis.");
        }
    }

}
