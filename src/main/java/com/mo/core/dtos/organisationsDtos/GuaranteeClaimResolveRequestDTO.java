package com.mo.core.dtos.organisationsDtos;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Schema(name = "GuaranteeClaimResolveRequest", description = "Payload to resolve a guarantee claim")
/**
 * GuaranteeClaimResolveRequestDTO
 *
 * Purpose: DTO used by moderators/admins to resolve filed guarantee claims.
 * Allows recording the resolution decision and notes.
 *
 * Important fields:
 * - `resolverId` (Long): id of the admin/moderator resolving the claim.
 * - `notes` (String): resolution details (e.g., approved refund reason).
 *
 * Frontend guidance:
 * - Restrict UI to admin/moderator roles.
 * - Show claim details and resolution options for the user to decide.
 */
public class GuaranteeClaimResolveRequestDTO {

    @NotNull
    @Schema(description = "Resolver identifier (moderator/admin user id)", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long resolverId;

    @Schema(description = "Resolution notes for the claim", example = "Approved refund due to broken item")
    private String notes;

    public Long getResolverId() {
        return resolverId;
    }

    public void setResolverId(Long resolverId) {
        this.resolverId = resolverId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}