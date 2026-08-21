package com.mo.core.dtos.organisationsDtos;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Schema(name = "ProductValidationRequest", description = "Payload for product validation by moderator")
/**
 * ProductValidationRequestDTO
 *
 * Purpose: DTO for moderator approval/rejection of submitted products.
 * Used in the content moderation workflow.
 *
 * Important fields:
 * - `approved` (Boolean): true to approve, false to reject.
 * - `comments` (String): optional feedback for the seller on why approved/rejected.
 *
 * Frontend guidance:
 * - Restrict to moderator role users.
 * - Show approval/rejection forms with required fields.
 * - Record moderator feedback so sellers can improve subsequent submissions.
 */
public class ProductValidationRequestDTO {

    @NotNull
    @Schema(description = "Whether the product is approved", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean approved;

    @Schema(description = "Optional comments from moderator", example = "Looks good")
    private String comments;

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}