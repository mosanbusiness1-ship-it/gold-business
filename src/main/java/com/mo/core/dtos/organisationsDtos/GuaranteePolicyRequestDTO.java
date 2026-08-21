package com.mo.core.dtos.organisationsDtos;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Schema(name = "GuaranteePolicyRequest", description = "Payload to configure or update an organisation guarantee policy")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
/**
 * GuaranteePolicyRequestDTO
 *
 * Purpose: DTO for setting up or updating guarantee policies for an organisation.
 * Guarantees provide buyer protection (e.g., refunds for defective items).
 *
 * Important fields:
 * - `durationMonths` (Integer): how long the guarantee lasts after purchase.
 * - `cost` (BigDecimal): the guarantee cost (may be included or additional).
 * - `coverage` (String): description of what is covered (e.g., shipping damage).
 * - `conditions` (String): terms for claiming (e.g., return within 30 days).
 *
 * Frontend guidance:
 * - Display guarantee details prominently on product pages to build buyer trust.
 * - Show the cost/terms clearly to avoid surprises at checkout.
 */
public class GuaranteePolicyRequestDTO {

    @NotNull
    @Schema(description = "Guarantee duration in months", example = "12", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer durationMonths;

    @NotNull
    @Schema(description = "Cost of the guarantee", example = "1500.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal cost;

    @NotNull
    @Schema(description = "Coverage description", example = "Shipping damage and fraud protection", requiredMode = Schema.RequiredMode.REQUIRED)
    private String coverage;

    @NotNull
    @Schema(description = "Guarantee conditions", example = "Return within 30 days with proof of issue", requiredMode = Schema.RequiredMode.REQUIRED)
    private String conditions;

    public Integer getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(Integer durationMonths) {
        this.durationMonths = durationMonths;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public String getCoverage() {
        return coverage;
    }

    public void setCoverage(String coverage) {
        this.coverage = coverage;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }
}