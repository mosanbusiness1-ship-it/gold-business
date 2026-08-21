package com.mo.core.dtos.organisationsDtos;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Schema(name = "CommissionConfig", description = "Payload to configure commission rates and mode for an organisation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
/**
 * CommissionConfigDTO
 *
 * Purpose: DTO for configuring how the platform will take commissions from
 * organisation transactions. Used by admin or finance teams.
 *
 * Important fields:
 * - `commissionOnPublish` (BigDecimal): fee when product is listed.
 * - `commissionOnSale` (BigDecimal): fee on each completed transaction.
 * - `commissionMode` (String): how commission is calculated (FIXED, PERCENTAGE).
 *
 * Frontend guidance:
 * - Generally restricted to admins/finance roles.
 * - Display effective commission rates on dashboards for org owners.
 */
public class CommissionConfigDTO {
    @JsonProperty("commission_on_publish")
    private BigDecimal commissionOnPublish;
    
    @JsonProperty("commission_on_sale")
    private BigDecimal commissionOnSale;
    
    @JsonProperty("commission_mode")
    private String commissionMode;
}