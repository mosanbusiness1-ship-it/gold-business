package com.mo.core.validators;

import jakarta.validation.*;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class ProductValidator {
    private final Validator validator;

    public ProductValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    public <T> void validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}

