package com.mo.core.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data @AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
/**
 * FoodSafetyReport
 *
 * Purpose: DTO representing the result of a food safety inspection or
 * compliance check for a food product.
 *
 * Fields:
 * - `productId` (Long): id of the inspected product.
 * - `isSafeToConsume` (boolean): whether the product is considered safe.
 * - `warningMessage` (String): optional text describing hazards or issues.
 *
 * Frontend guidance:
 * - Display `isSafeToConsume` prominently in product safety badges.
 * - Show `warningMessage` to users when the product is unsafe or requires attention.
 */
public class FoodSafetyReport {
    private Long productId;
    private boolean isSafeToConsume;
    private String warningMessage;
}