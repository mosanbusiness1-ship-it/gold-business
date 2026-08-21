package com.mo.core.dtos.auctions;

import java.time.LocalDateTime;

import com.mo.core.dtos.userNeedsDTO.AbstractUserNeedDto;

import lombok.Data;

@Data
/**
 * AuctionRequest
 *
 * Purpose: DTO used by the frontend to create or update an auction.
 * This object represents scheduling and ownership information for an auction
 * and includes an embedded need (`AbstractUserNeedDto`) that describes what
 * item or requirement is being auctioned.
 *
 * Important fields:
 * - `startedAt` (LocalDateTime): optional, auction start timestamp.
 * - `endAt` (LocalDateTime): optional, auction end timestamp.
 * - `isActived` (boolean): whether the auction is active.
 * - `userId` (Long): id of the user creating/owning the auction (required for create).
 * - `need` (AbstractUserNeedDto): the need/product descriptor to be matched in the auction.
 *
 * Frontend guidance:
 * - Use this DTO to POST to the auction creation endpoint and to PUT updates.
 * - Provide `userId` as the auth/owner reference and fill `need` with the same
 *   shape used when creating needs in the platform (see `AbstractUserNeedDto`).
 * - Dates should be ISO-8601 strings (e.g. `2026-07-21T12:00:00`).
 *
 * Example (JSON payload):
 * {
 *   "startedAt": "2026-07-21T12:00:00",
 *   "endAt": "2026-07-22T12:00:00",
 *   "isActived": true,
 *   "userId": 10,
 *   "need": { ... }
 * }
 */
public class AuctionRequest {
    private LocalDateTime startedAt;
    private LocalDateTime endAt;
    private boolean isActived;
    private Long userId;

    private AbstractUserNeedDto need;

}