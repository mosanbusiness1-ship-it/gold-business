package com.mo.core.dtos.userNeedsDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.enums.NeedType;
import com.mo.core.enums.RealEstateType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
/**
 * RealEstateNeedDto
 *
 * Purpose: DTO for real estate needs (property searches). Extends
 * `AbstractUserNeedDto` with address, sizing and property-type metadata to help
 * match listings and schedule visits.
 *
 * Notable fields:
 * - `address`, `city`, `surfaceArea`, `roomCount`, `bathroomCount`.
 * - `realEstateType` (RealEstateType), `isForRent`, `isForSale`.
 * - `photoUrls` for visual context.
 *
 * Frontend guidance:
 * - Use these fields to pre-fill search filters and to show matching property
 *   cards to potential sellers or agents.
 */
public class RealEstateNeedDto extends AbstractUserNeedDto {

    @JsonProperty("mandatory_fields")
    private List<String> mandatoryFields = new ArrayList<>();

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

    @JsonProperty("photo_urls")
    private List<String> photoUrls;

    @JsonProperty("is_for_rent")
    private Boolean isForRent;

    @JsonProperty("is_for_sale")
    private Boolean isForSale;

    @JsonProperty("construction_year")
    private Integer constructionYear;

    @JsonProperty("energy_class")
    private String energyClass;

}