package com.mo.core.dtos.productsDtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.enums.Currency;
import com.mo.core.enums.FoodCategory;
import com.mo.core.enums.ProductType;

@Data
@NoArgsConstructor
/**
 * FoodProductDto
 *
 * Purpose: DTO for food items, including perishability and nutrition data.
 *
 * Notable fields:
 * - `category` (FoodCategory): type of food (e.g., BAKERY, PRODUCE).
 * - `expiryDate` (LocalDate): use to display freshness and to filter out
 *   expired items on the frontend.
 * - `nutritionalInfo` (String): freeform nutritional text or JSON string.
 * - `organic` / `glutenFree` / `weight`: important for filter facets.
 *
 * Frontend guidance:
 * - Show `expiryDate` prominently and warn users when an item is near expiry.
 * - Provide nutrition panel rendering `nutritionalInfo` when present.
 */
public class FoodProductDto extends AbstractProductDto {
    private FoodCategory category;

    @JsonProperty("expiry_date")
    private LocalDate expiryDate;

    @JsonProperty("nutritional_info")
    private String nutritionalInfo;

    private Boolean organic;

    @JsonProperty("luten_free")
    private Boolean glutenFree;

    private Double weight;
}
