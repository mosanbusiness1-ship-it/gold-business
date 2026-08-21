package com.mo.core.validators;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.mo.core.model.products.AbstractProduct;
import com.mo.core.model.products.ElectronicProduct;
import com.mo.core.model.products.FashionProduct;
import com.mo.core.model.products.FoodProduct;
import com.mo.core.model.products.RealEstateProduct;
import com.mo.core.model.products.ServiceProduct;
import com.mo.core.model.products.VehicleProduct;

@Component
public class BusinessValidationRegistry {

    private final Map<Class<? extends AbstractProduct>, BusinessValidator<? extends AbstractProduct>> validators = new HashMap<>();

    public BusinessValidationRegistry() {
        register(ServiceProduct.class, new ServiceProductValidator());
        register(RealEstateProduct.class, new RealEstateProductValidator());
        register(VehicleProduct.class, new VehicleProductValidator());
        register(ElectronicProduct.class, new ElectronicProductValidator());
        register(FashionProduct.class, new FashionProductValidator());
        register(FoodProduct.class, new FoodProductValidator()); // Si applicable
    }

    public <T extends AbstractProduct> void register(Class<T> clazz, BusinessValidator<T> validator) {
        validators.put(clazz, validator);
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractProduct> void validate(T product) {
        BusinessValidator<T> validator = (BusinessValidator<T>) validators.get(product.getClass());
        if (validator != null) {
            validator.validate(product);
        }
    }
}
