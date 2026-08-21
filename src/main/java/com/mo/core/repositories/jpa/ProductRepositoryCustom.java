package com.mo.core.repositories.jpa;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.mo.core.enums.ProductType;
import com.mo.core.model.products.AbstractProduct;

@Repository
public interface ProductRepositoryCustom {
    List<AbstractProduct> searchWithFilters(String name, ProductType type, BigDecimal minPrice, BigDecimal maxPrice);
}

