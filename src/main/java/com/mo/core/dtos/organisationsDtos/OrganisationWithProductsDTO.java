package com.mo.core.dtos.organisationsDtos;

import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.mo.core.dtos.productsDtos.AbstractProductDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
/**
 * OrganisationWithProductsDTO
 *
 * Purpose: combined response DTO bundling an organisation with its associated
 * products. Used when returning a single org with all products in one call.
 *
 * Fields:
 * - `organisation` (OrganisationDTO): the org summary.
 * - `products` (Set<JsonNode>): polymorphic product list (AbstractProductDto subtypes).
 *
 * Frontend guidance:
 * - Use to render org profile pages with full product inventory in one view.
 * - Each product in `products` deserializes to the correct concrete type
 *   based on its `type` field (SERVICE, VEHICLE, ELECTRONIC, etc.).
 */
public class OrganisationWithProductsDTO {
    private OrganisationDTO organisation;
    private Set<JsonNode> products;
}
