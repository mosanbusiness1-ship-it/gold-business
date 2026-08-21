package com.mo.core.dtos.organisationsDtos;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Schema(name = "EscrowCreateRequest", description = "Payload to create an escrow transaction for a product sale")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
/**
 * EscrowCreateRequestDTO
 *
 * Purpose: DTO for initiating an escrow transaction. Used to hold payment funds
 * during a transaction and release them upon conditions being met.
 *
 * Important fields:
 * - `productId` (Long): the product being purchased (payment is held for this).
 * - `amount` (BigDecimal): funds to hold in escrow.
 * - `currency` (String): ISO-4217 code (e.g., XOF, USD).
 * - `metadata` (String): optional JSON metadata (e.g., order ID) for backend tracking.
 *
 * Frontend guidance:
 * - Ensure `amount` matches the negotiated/expected price before escrow creation.
 * - Use `metadata` to link escrow to orders or purchase contexts.
 * - After creation, monitor escrow status via `EscrowTransactionDTO` responses.
 */
public class EscrowCreateRequestDTO {

    @NotNull
    @Schema(description = "Product identifier for the escrowed sale", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productId;

    @NotNull
    @Schema(description = "Escrow amount to hold", example = "12000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(description = "ISO currency code", example = "XOF")
    private String currency;

    @Schema(description = "Optional metadata attached to the escrow transaction", example = "{\"orderId\":12345}")
    private String metadata;

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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}