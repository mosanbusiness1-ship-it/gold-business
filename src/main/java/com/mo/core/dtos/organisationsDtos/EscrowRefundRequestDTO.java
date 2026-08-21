package com.mo.core.dtos.organisationsDtos;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Schema(name = "EscrowRefundRequest", description = "Payload to request a refund of an escrow transaction")
/**
 * EscrowRefundRequestDTO
 *
 * Purpose: DTO for requesting a refund of escrowed funds. Used when a buyer
 * returns items, disputes arise, or transaction conditions are not met.
 *
 * Important fields:
 * - `reason` (String): explanation for the refund request.
 *
 * Frontend guidance:
 * - Collect the refund reason from the user (return, dispute resolution, etc.).
 * - Display refund confirmation after successful submission.
 */
public class EscrowRefundRequestDTO {

    @NotNull
    @Schema(description = "Reason for the refund", example = "Customer returned the item", requiredMode = Schema.RequiredMode.REQUIRED)
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}