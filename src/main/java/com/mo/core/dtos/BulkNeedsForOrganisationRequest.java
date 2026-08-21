package com.mo.core.dtos;

import com.mo.core.dtos.userNeedsDTO.AbstractUserNeedDto;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * BulkNeedsForOrganisationRequest
 *
 * Purpose: request DTO for submitting a batch of user needs to a single
 * organisation. Useful when the frontend wants to create or link multiple needs
 * in one request.
 *
 * Fields:
 * - `needs` (List<AbstractUserNeedDto>): list of need payloads describing the
 *   requested items or services.
 *
 * Frontend guidance:
 * - Use this DTO when shipping multiple needs at once, such as during a bulk
 *   import or marketplace creation flow.
 * - Ensure each need entry includes a valid `type` for polymorphic deserialization.
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record BulkNeedsForOrganisationRequest(
        @NotEmpty List<AbstractUserNeedDto> needs
) {}