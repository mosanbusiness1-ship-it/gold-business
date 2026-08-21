package com.mo.core.model.specifications;


import org.springframework.data.jpa.domain.Specification;

import com.mo.core.model.products.AbstractProduct;

public interface ProductSpecification {
    
    Specification<AbstractProduct> toSpecification();

    static ProductSpecification of(Specification<AbstractProduct> spec) {
        return () -> spec;
    }

    default ProductSpecification and(ProductSpecification other) {
        return () -> toSpecification().and(other.toSpecification());
    }

    default ProductSpecification or(ProductSpecification other) {
        return () -> toSpecification().or(other.toSpecification());
    }
}

