package com.mo.core.dtos.userNeedsDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.enums.FashionType;
import com.mo.core.enums.NeedType;
import com.mo.core.enums.SizeSystem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
/**
 * FoodNeedDto
 *
 * Purpose: DTO for food-related needs (perishable goods). Extends
 * `AbstractUserNeedDto` and includes attributes relevant to food items and
 * supplier responses.
 *
 * Notable fields:
 * - `mandatoryFields` (List<String>): seller-required fields.
 * - additional fields inherited or reused from fashion-like structure for now;
 *   treat `mandatoryFields` and `photoUrls` as primary hints for suppliers.
 *
 * Frontend guidance:
 * - Display expiry and freshness information when available (if backend adds it).
 */
public class FoodNeedDto extends AbstractUserNeedDto {

    @JsonProperty("mandatory_fields")
    private List<String> mandatoryFields = new ArrayList<>();

    @JsonProperty("fashion_type")
    private FashionType fashionType;

    private String size;

    @JsonProperty("size_system")
    private SizeSystem sizeSystem;

    private String color;

    private String material;

    private String brand;

    @JsonProperty("target_gender")
    private String targetGender;

}