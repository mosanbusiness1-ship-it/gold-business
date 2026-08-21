package com.mo.core.dtos.organisationsDtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * OrganisationReviewRequest
 *
 * Purpose: DTO used by the frontend to submit a review for an organisation.
 * Allows buyers/users to rate and comment on their experience with a seller.
 *
 * Important fields:
 * - `rating` (Integer): 1-5 star rating (validated as 1-5).
 * - `title` (String): short headline for the review.
 * - `comment` (String): full review text/comment.
 * - `isVerifiedPurchase` (boolean): whether the reviewer bought from this org.
 *
 * Frontend guidance:
 * - Show a 5-star picker for rating selection.
 * - Mark reviews as 'verified purchase' to highlight credible feedback.
 * - Validate rating is in [1,5] before sending.
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrganisationReviewRequest {

    @NotNull
    @Min(1)
    @Max(5)
    @Schema(description = "Rating between 1 and 5", example = "4")
    private Integer rating;

    @Schema(description = "Title of the review", example = "Excellent seller")
    private String title;

    @Schema(description = "Text comment for the review", example = "Fast delivery and good communication")
    private String comment;

    @Schema(description = "Whether the review is from a verified purchase", example = "true")
    private boolean isVerifiedPurchase;

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isVerifiedPurchase() {
        return isVerifiedPurchase;
    }

    public void setVerifiedPurchase(boolean verifiedPurchase) {
        isVerifiedPurchase = verifiedPurchase;
    }
}