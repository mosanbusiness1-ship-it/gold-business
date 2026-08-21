package com.mo.core.factories;

import com.mo.core.dtos.productsDtos.AbstractProductDto;
import com.mo.core.model.products.AbstractProduct;

public interface ProductFactory {
    AbstractProduct create(AbstractProductDto dto);
}
