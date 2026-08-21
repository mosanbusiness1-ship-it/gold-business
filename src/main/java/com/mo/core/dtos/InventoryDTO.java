package com.mo.core.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data @AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
/**
 * InventoryDTO
 *
 * Purpose: DTO representing inventory metadata for a product item.
 *
 * Fields:
 * - `productId` (Long): id of the product in inventory.
 * - `inventoryCode` (String): stock keeping unit or warehouse code.
 * - `storageLocation` (String): location within the inventory system.
 *
 * Frontend guidance:
 * - Use this DTO in stock management or warehouse dashboard UI.
 */
public class InventoryDTO {
    private Long productId;
    private String inventoryCode;
    private String storageLocation;
}