package com.mo.core.dtos.auctions;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.model.auctions.BidStatus;

/**
 * BidResponseDTO
 *
 * Purpose: response DTO returned to the frontend after a bid operation or when
 * retrieving bid history for an auction. It contains identifying fields and
 * the bid amount/status so the UI can display current state and timestamps.
 *
 * Important fields:
 * - `id` (Long): internal bid id.
 * - `auctionId` (Long): id of the auction this bid belongs to.
 * - `productId` (Long): id of the product involved in the auction (if any).
 * - `bidderId` (Long): id of the user who placed the bid.
 * - `amount` (BigDecimal): bid amount in smallest currency unit or decimal form.
 * - `status` (BidStatus): enum representing bid lifecycle (e.g. PENDING, WON).
 * - `createdAt` (LocalDateTime): timestamp when bid was placed.
 *
 * Frontend guidance:
 * - Render `amount` with the appropriate currency formatting configured elsewhere.
 * - Use `status` to decide allowed UI actions (e.g. cancel, increase bid).
 * - When listing bids, sort by `createdAt` descending for latest-first views.
 *
 * Example (JSON):
 * {
 *   "id": 123,
 *   "auction_id": 45,
 *   "product_id": 451,
 *   "bidder_id": 10,
 *   "amount": 15000.00,
 *   "status": "PENDING",
 *   "created_at": "2026-07-21T12:34:56"
 * }
 */
public class BidResponseDTO {
    private Long id;

    @JsonProperty("auction_id")
    private Long auctionId;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("bidder_id")
    private Long bidderId;

    private BigDecimal amount;
    private BidStatus status;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Long auctionId) {
        this.auctionId = auctionId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getBidderId() {
        return bidderId;
    }

    public void setBidderId(Long bidderId) {
        this.bidderId = bidderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BidStatus getStatus() {
        return status;
    }

    public void setStatus(BidStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}