package com.mo.core.dtos;


import com.mo.core.dtos.userNeedsDTO.AbstractUserNeedDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * CreateNeedWithOrganisationsRequest
 *
 * Purpose: request DTO used by the frontend to create a need and simultaneously
 * associate it with one or more organisations.
 *
 * Fields:
 * - `need` (AbstractUserNeedDto): the need payload describing the requested item or service.
 * - `organisationIds` (List<Long>): list of organisation ids to attach the need to.
 *
 * Frontend guidance:
 * - Use this DTO when a buyer wants to publish a need across multiple organisations.
 * - Ensure organisationIds contains valid organisation ids and the user has
 *   permission to post needs to those organisations.
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CreateNeedWithOrganisationsRequest(
     @NotNull AbstractUserNeedDto need,
     @NotEmpty List<Long> organisationIds
) {}
