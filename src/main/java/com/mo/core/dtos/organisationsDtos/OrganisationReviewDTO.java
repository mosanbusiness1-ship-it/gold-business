package com.mo.core.dtos.organisationsDtos;

import java.time.LocalDateTime;

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
 * OrganisationReviewDTO
 *
 * Purpose: response DTO representing a review that was submitted for an
 * organisation. Contains reviewer info, rating, text and metadata.
 *
 * Key fields:
 * - `id` (Long): unique review id.
 * - `organisationId` (Long): org being reviewed.
 * - `reviewerId` (Long): who submitted the review.
 * - `rating`, `title`, `comment`: review content.
 * - `isVerifiedPurchase` (boolean): credibility indicator.
 * - `status` (String): moderation status (PENDING, APPROVED, etc.).
 * - `createdAt` (LocalDateTime): when review was posted.
 *
 * Frontend guidance:
 * - Display reviews on organisation profile pages.
 * - Sort by `createdAt` descending (newest first) or by relevance.
 */
public class OrganisationReviewDTO {
    private Long id;
    
    private Long organisationId;
    
    private Long reviewerId;
    
    @JsonProperty("rating")
    private Integer rating;
    
    private String title;
    
    private String comment;
    
    @JsonProperty("is_verified_purchase")
    private boolean isVerifiedPurchase;
    
    private String status;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}