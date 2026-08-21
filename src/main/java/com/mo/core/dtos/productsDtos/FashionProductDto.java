package com.mo.core.dtos.productsDtos;



import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.enums.Currency;
import com.mo.core.enums.FashionType;
import com.mo.core.enums.ProductType;
import com.mo.core.enums.SizeSystem;

import lombok.*;

@Data
@NoArgsConstructor
/**
 * FashionProductDto
 *
 * Purpose: DTO for fashion items (clothing, accessories). Adds sizing,
 * material and brand information to `AbstractProductDto`.
 *
 * Notable fields:
 * - `fashionType` (FashionType): category such as TOPS, BOTTOMS, ACCESSORIES.
 * - `size`, `sizeSystem` (SizeSystem): sizing and system for correct UI
 *   selection widgets.
 * - `color`, `material`, `brand`, `targetGender`: display and filter data.
 *
 * Frontend guidance:
 * - Use `sizeSystem` to present correct size labels and conversion helpers.
 * - Offer color and material swatches using `color` and `material` fields.
 */
public class FashionProductDto extends AbstractProductDto {

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
