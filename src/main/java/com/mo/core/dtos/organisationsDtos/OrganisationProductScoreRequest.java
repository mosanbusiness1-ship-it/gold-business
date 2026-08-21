package com.mo.core.dtos.organisationsDtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * OrganisationProductScoreRequest
 *
 * Purpose: DTO for moderators to assign a quality score/rating to a product
 * within an organisation. Used in content quality management.
 *
 * Important fields:
 * - `score` (Integer): 1-5 star score.
 * - `comment` (String): optional moderator notes on quality.
 *
 * Frontend guidance:
 * - Show in moderation dashboards for quality review workflows.
 * - Display score prominently in product details if visible to end users.
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrganisationProductScoreRequest {

    @NotNull
    @Min(1)
    @Max(5)
    private Integer score;

    private String comment;

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}