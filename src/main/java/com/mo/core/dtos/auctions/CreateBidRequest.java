package com.mo.core.dtos.auctions;

import java.math.BigDecimal;

/**
 * CreateBidRequest
 *
 * Purpose: DTO used by the frontend to submit a new bid for an auction.
 *
 * Important fields:
 * - `productId` (Long): id of the product being bid on.
 * - `bidderId` (Long): id of the user placing the bid.
 * - `amount` (BigDecimal): bid amount.
 *
 * Frontend guidance:
 * - Validate `amount` (positive, within allowed limits) before sending.
 * - Ensure `bidderId` matches authenticated user or omit if server derives it from auth.
 *
 * Example (JSON):
 * { "productId": 451, "bidderId": 10, "amount": 15000.00 }
 */
public class CreateBidRequest {
    private Long productId;
    private Long bidderId;
    private BigDecimal amount;

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
}