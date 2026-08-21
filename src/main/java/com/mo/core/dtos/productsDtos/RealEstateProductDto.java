package com.mo.core.dtos.productsDtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.enums.ProductType;
import com.mo.core.enums.RealEstateType;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
/**
 * RealEstateProductDto
 *
 * Purpose: DTO for real estate listings. Extends `AbstractProductDto` with
 * property-specific fields used by listing and detail pages.
 *
 * Notable fields:
 * - `address`, `city`: primary location fields.
 * - `surfaceArea`, `roomCount`, `bathroomCount`: key property metrics.
 * - `realEstateType` (RealEstateType): classification (APARTMENT, HOUSE...).
 * - `isForRent` / `isForSale`: listing intent.
 * - `constructionYear`, `energyClass`: additional metadata for filters.
 *
 * Frontend guidance:
 * - Use `surfaceArea` and room counts to construct summary cards and
 *   comparison views.
 * - Use `isForRent`/`isForSale` to toggle action buttons (contact, schedule visit).
 */
public class RealEstateProductDto extends AbstractProductDto {

    private String address;
    private String city;

    @JsonProperty("surface_area")
    private Double surfaceArea;

    @JsonProperty("room_count")
    private Integer roomCount;

    @JsonProperty("bathroom_count")
    private Integer bathroomCount;

    @JsonProperty("real_estate_type")
    private RealEstateType realEstateType;

    @JsonProperty("is_for_rent")
    private Boolean isForRent;

    @JsonProperty("is_for_sale")
    private Boolean isForSale;

    @JsonProperty("construction_year")
    private Integer constructionYear;

    @JsonProperty("energy_class")
    private String energyClass;

}
