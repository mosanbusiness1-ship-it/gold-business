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
 * FashionNeedDto
 *
 * Purpose: DTO representing fashion-related needs (clothing, accessories).
 * Extends `AbstractUserNeedDto` with sizing, color and material attributes.
 *
 * Notable fields:
 * - `mandatoryFields` (List<String>): fields sellers must provide in responses.
 * - `fashionType` (FashionType), `size`, `sizeSystem`, `color`, `material`, `brand`.
 * - `targetGender` for appropriate targeting and filtering.
 *
 * Frontend guidance:
 * - Use `sizeSystem` to show correct size pickers and conversions.
 */
public class FashionNeedDto extends AbstractUserNeedDto {

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