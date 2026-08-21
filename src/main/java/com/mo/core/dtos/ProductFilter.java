package com.mo.core.dtos;

import java.math.BigDecimal;

import com.mo.core.enums.ProductType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
/**
 * ProductFilter
 *
 * Purpose: DTO for filtering product listings on the backend.
 *
 * Fields:
 * - `type` (ProductType): restrict products by type.
 * - `minPrice` (BigDecimal): filter products with price above or equal to this value.
 *
 * Frontend guidance:
 * - Use this DTO when building product search or filter controls.
 */
public class ProductFilter {
    private ProductType type;
    private BigDecimal minPrice;
}