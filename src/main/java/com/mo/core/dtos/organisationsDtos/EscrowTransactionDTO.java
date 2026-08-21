package com.mo.core.dtos.organisationsDtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Schema(name = "EscrowTransaction", description = "Escrow transaction details for organisation escrow status")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
/**
 * EscrowTransactionDTO
 *
 * Purpose: response DTO representing the state of an escrow transaction.
 * Returned when retrieving escrow details or after escrow operations.
 *
 * Key fields:
 * - `id` (Long): unique escrow transaction identifier.
 * - `productId` (Long): the product being held in escrow.
 * - `amount` (BigDecimal): funds held.
 * - `status` (String): current status (HELD, RELEASED, REFUNDED, DISPUTED).
 * - `metadata` (String): optional context attached at creation.
 * - `createdAt` / `releasedAt` (LocalDateTime): timeline information.
 *
 * Frontend guidance:
 * - Poll or subscribe to updates using this DTO to show transaction progress.
 * - Display status badges (HELD = funds locked, RELEASED = completed).
 */
public class EscrowTransactionDTO {

    @Schema(description = "Escrow transaction identifier", example = "789")
    private Long id;

    @Schema(description = "Product identifier", example = "456")
    private Long productId;

    @Schema(description = "Held amount", example = "12000.00")
    private BigDecimal amount;

    @Schema(description = "Transaction status", example = "HELD")
    private String status;

    @Schema(description = "Optional metadata attached to the escrow transaction", example = "{\"orderId\":12345}")
    private String metadata;

    @Schema(description = "Creation timestamp", example = "2026-07-18T20:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Release timestamp when funds were released", example = "2026-07-18T21:00:00")
    private LocalDateTime releasedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReleasedAt() {
        return releasedAt;
    }

    public void setReleasedAt(LocalDateTime releasedAt) {
        this.releasedAt = releasedAt;
    }
}