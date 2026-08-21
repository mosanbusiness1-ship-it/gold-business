package com.mo.core.dtos.organisationsDtos;

import java.util.List;
import java.util.Set;

import com.mo.core.enums.OrganisationType;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * OrganisationDTO
 *
 * Purpose: lightweight record-based DTO representing an organisation summary.
 * Used for quick responses listing organisations or when full details are not needed.
 *
 * Key fields:
 * - `id` (Long): organisation identifier.
 * - `name` (String): organisation name.
 * - `type` (OrganisationType): classification.
 * - `productIds` (Set<Long>): ids of products managed by this org.
 * - `childrenIds` (List<Long>): ids of child organisations (hierarchical structure).
 *
 * Frontend guidance:
 * - Use to render organisation cards in list views.
 * - Click an org card to load full details via OrganisationDTO or other DTOs.
 * */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record OrganisationDTO(
    Long id,
    String name,
    OrganisationType type,
    Set<Long> productIds,
    List<Long> childrenIds
) {}
