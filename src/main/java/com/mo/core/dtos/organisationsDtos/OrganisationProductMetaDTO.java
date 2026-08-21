package com.mo.core.dtos.organisationsDtos;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.enums.ProductApprovalStatus;
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
 * OrganisationProductMetaDTO
 *
 * Purpose: DTO tracking the lifecycle of a product submission within an
 * organisation (e.g., pending approval, approved, rejected, scored).
 *
 * Key fields:
 * - `organisationId` (Long), `productId` (Long): identifies the product.
 * - `approvalStatus` (ProductApprovalStatus): current approval state.
 * - `submittedAt` / `validatedAt` (LocalDateTime): timeline of moderation.
 * - `validationComments` (String): moderator feedback.
 * - `orgScore` (Integer): quality score if assigned.
 *
 * Frontend guidance:
 * - Use for moderation dashboards showing product status.
 * - Display approval timeline and feedback to sellers for improvement.
 */
public class OrganisationProductMetaDTO {
    private Long organisationId;
    
    private Long productId;
    
    @JsonProperty("approval_status")
    private ProductApprovalStatus approvalStatus;
    
    @JsonProperty("submitted_at")
    private LocalDateTime submittedAt;
    
    @JsonProperty("validated_at")
    private LocalDateTime validatedAt;
    
    @JsonProperty("validation_comments")
    private String validationComments;
    
    @JsonProperty("org_score")
    private Integer orgScore;
}