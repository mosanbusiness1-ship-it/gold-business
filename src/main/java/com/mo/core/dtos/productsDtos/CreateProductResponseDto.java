package com.mo.core.dtos.productsDtos;

import java.util.List;
import java.util.Map;

import com.mo.core.dtos.productsDtos.AbstractProductDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Schema(description = "Response returned after a product is created")
/**
 * CreateProductResponseDto
 *
 * Purpose: Response returned by the backend after a successful product
 * creation. This DTO bundles the saved product representation (concrete
 * `AbstractProductDto` subtype) with matching needs discovered by the system.
 *
 * Fields:
 * - `savedProduct` (AbstractProductDto): the persisted product as returned
 *    by the backend. The concrete subtype depends on `type` (SERVICE, VEHICLE,
 *    ELECTRONIC, ...).
 * - `strictlyMatchingNeeds` (List<Map<String,Object>>): needs that exactly
 *    match the product and may be used to notify or auto-match buyers.
 * - `similarNeeds` (List<Map<String,Object>>): related needs that may be
 *    useful for cross-promotion or suggested matching.
 *
 * Frontend guidance:
 * - Use `savedProduct` to render the created product detail page immediately
 *   after creation.
 * - Use `strictlyMatchingNeeds` to populate quick actions like "Contact
 *   matching buyers" or to trigger notifications.
 */
public class CreateProductResponseDto {

    @Schema(description = "Created product data", oneOf = {
            ServiceProductDto.class,
            VehicleProductDto.class,
            ElectronicProductDto.class,
            FashionProductDto.class,
            FoodProductDto.class,
            RealEstateProductDto.class
    })
    private AbstractProductDto savedProduct;

    @Schema(description = "List of strictly matching needs")
    private List<Map<String, Object>> strictlyMatchingNeeds;

    @Schema(description = "List of similar needs")
    private List<Map<String, Object>> similarNeeds;

}