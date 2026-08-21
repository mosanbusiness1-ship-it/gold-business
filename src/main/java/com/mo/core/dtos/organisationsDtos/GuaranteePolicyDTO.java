package com.mo.core.dtos.organisationsDtos;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
/**
 * GuaranteePolicyDTO
 *
 * Purpose: response DTO representing the persisted guarantee policy for an
 * organisation. Returned after policy creation or when fetching policy details.
 *
 * Key fields:
 * - `id` (Long): policy identifier.
 * - `organisationId` (Long): the org this policy applies to.
 * - `durationMonths`, `cost`, `coverage`, `conditions`: policy terms.
 * - `active` (boolean): whether the policy is currently in effect.
 *
 * Frontend guidance:
 * - Display on organisation/product pages to build buyer confidence.
 * - Show policy cost and expiry info at checkout.
 */
public class GuaranteePolicyDTO {
    private Long id;
    
    private Long organisationId;
    
    @JsonProperty("duration_months")
    private Integer durationMonths;
    
    private BigDecimal cost;
    
    private String coverage;
    
    private String conditions;
    
    private boolean active;
}