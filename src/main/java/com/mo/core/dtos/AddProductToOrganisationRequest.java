package com.mo.core.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
/**
 * AddProductToOrganisationRequest
 *
 * Purpose: request DTO used by frontend clients to attach an existing product to
 * an organisation.
 *
 * Fields:
 * - `organisationId` (Long): id of the target organisation.
 * - `productId` (Long): id of the product to attach.
 *
 * Frontend guidance:
 * - Use this DTO when an organisation administrator wants to include a product
 *   in their catalogue.
 * - Validate the selected organisation and product IDs before sending.
 */
public class AddProductToOrganisationRequest {
    @NotNull
    private Long organisationId;

    @NotNull
    private Long productId;

}
