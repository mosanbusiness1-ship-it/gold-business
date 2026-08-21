package com.mo.core.dtos;

import com.mo.core.enums.OrganisationType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
/**
 * OrganisationSearchCriteria
 *
 * Purpose: DTO used to encapsulate search filters when querying organisations.
 *
 * Fields:
 * - `name` (String): organisation name search term.
 * - `type` (OrganisationType): organisation classification filter.
 * - `minProductCount` (Long): minimum number of products to include in results.
 *
 * Frontend guidance:
 * - Use this DTO to build organisation search forms or filter panels.
 * - Leave fields null when no filter is needed.
 */
public class OrganisationSearchCriteria {
    private String name;
    private OrganisationType type;
    private Long minProductCount;
}