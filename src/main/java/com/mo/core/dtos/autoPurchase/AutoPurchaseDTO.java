package com.mo.core.dtos.autoPurchase;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.enums.Currency;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
/**
 * AutoPurchaseDTO
 *
 * Purpose: Data transfer object representing an auto-purchase transaction request payload.
 * Used internally in Kafka messaging to process automatic purchases between users when
 * product needs are explicitly matched. This DTO bridges the matching engine output to
 * the payment gateway integration.
 *
 * Fields:
 * - `externalWalletIdSrc` (String): identifier of the buyer's external wallet (source).
 *   This is not a secret; only an identifier for routing the transaction.
 * - `externalWalletIdDest` (String): identifier of the seller's external wallet (destination).
 *   This is not a secret; only an identifier for routing the transaction.
 * - `productId` (Long): unique identifier of the product being purchased.
 * - `productName` (String): human-readable name of the product for transaction logs and notifications.
 * - `productQuantity` (int): quantity of the product to be purchased.
 * - `currency` (Currency enum): the currency type (e.g., USD, EUR) for the transaction.
 * - `amount` (BigDecimal): total transaction amount, computed as price × quantity.
 *
 * Frontend Guidance:
 * - This DTO is primarily used server-side for Kafka messaging; frontend typically does not
 *   directly construct this object.
 * - Frontend may receive summary data about auto-purchase transactions in notification responses.
 * - Treat external wallet identifiers as non-sensitive identifiers only; do not expose as secrets.
 * - Display transaction history showing product name, quantity, amount, and currency.
 *
 * Security Note:
 * - External wallet IDs are identifiers, not access credentials.
 * - The payment gateway handles encryption and account verification.
 */
public class AutoPurchaseDTO {
	
	@JsonProperty("wallet_code_src")
	private String externalWalletIdSrc;
	
	@JsonProperty("wallet_code_dest")
	private String externalWalletIdDest;
	
	@JsonProperty("product_id")
	private Long productId;
	
	@JsonProperty("product_name")
	private String productName;
	
	@JsonProperty("product_quantity")
	private int productQuantity;
	
	private Currency currency;
	private BigDecimal amount;

}