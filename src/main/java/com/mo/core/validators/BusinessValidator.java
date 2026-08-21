package com.mo.core.validators;

import org.springframework.stereotype.Component;

import com.mo.core.model.products.AbstractProduct;
@Component
public interface BusinessValidator<T extends AbstractProduct> {
    void validate(T product);
}

