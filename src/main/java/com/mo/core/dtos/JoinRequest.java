package com.mo.core.dtos;

import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * JoinRequest
 *
 * Purpose: DTO used when a user requests to join an organisation or when the
 * system tracks a join request status.
 *
 * Fields:
 * - `userId` (Long): id of the requesting user.
 * - `organisationId` (Long): id of the organisation being joined.
 * - `requestedAt` (LocalDateTime): timestamp when the request was made.
 * - `approved` / `rejected` (boolean): current approval status.
 *
 * Frontend guidance:
 * - Render this object in organisation membership workflows.
 * - Use `approved` and `rejected` fields to display pending, accepted or denied state.
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class JoinRequest {
    private Long userId;

    private Long organisationId;

    private LocalDateTime requestedAt;

    private boolean approved;

    private boolean rejected;
}
