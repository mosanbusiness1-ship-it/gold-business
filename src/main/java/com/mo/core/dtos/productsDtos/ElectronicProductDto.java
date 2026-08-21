package com.mo.core.dtos.productsDtos;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.enums.Currency;
import com.mo.core.enums.ElectronicType;
import com.mo.core.enums.ProductType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
/**
 * ElectronicProductDto
 *
 * Purpose: DTO for electronic products. Extends `AbstractProductDto` with
 * electronics-specific metadata used by the frontend to render details and
 * filters.
 *
 * Notable fields:
 * - `electronicType` (ElectronicType): subcategory (e.g., PHONE, LAPTOP).
 * - `brand`, `model`, `specifications`: text fields for display and search.
 * - `warrantyPeriod`: warranty information to show in product details.
 *
 * Frontend guidance:
 * - Use `brand`/`model` and `specifications` for search and comparison UI.
 * - Display `warrantyPeriod` prominently for buyer trust signals.
 */
public class ElectronicProductDto extends AbstractProductDto {

    @JsonProperty("electronic_type")
    private ElectronicType electronicType;
    
    private String brand;
    private String model;
    private String specifications; // JSON string or another simple format

    @JsonProperty("warranty_period")
    private String warrantyPeriod;
}
