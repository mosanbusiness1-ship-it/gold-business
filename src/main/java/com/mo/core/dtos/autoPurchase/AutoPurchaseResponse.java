package com.mo.core.dtos.autoPurchase;

import com.mo.core.enums.Currency;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
/**
 * AutoPurchaseResponse
 *
 * Purpose: Response payload returned by the payment gateway when processing an auto-purchase transaction.
 * Represents the outcome of a transaction submitted to an external payment processor or wallet service.
 * This DTO bridges payment provider responses to internal transaction logging and notification systems.
 *
 * Fields:
 * - `amount` (double): total amount processed by the payment gateway.
 * - `currency` (Currency enum): currency type in which the transaction was processed.
 * - `srcChannel` (String): source channel/wallet identifier at the payment provider (buyer side).
 * - `destChannel` (String): destination channel/wallet identifier at the payment provider (seller side).
 * - `reason` (String): detailed message or reason code from the payment provider
 *   (e.g., "Transaction approved", "Insufficient funds", "Account suspended").
 * - `success` (boolean): flag indicating transaction approval (true) or rejection (false).
 *
 * Frontend Guidance:
 * - This DTO typically arrives as webhook notification data from the payment provider.
 * - Frontend should display user-friendly transaction status:
 *   - If success=true: "Transaction completed successfully"
 *   - If success=false: Parse `reason` field and display specific error to user.
 * - Enable transaction history viewing with status, amount, and provider response date.
 * - Trigger follow-up notifications based on success status.
 *
 * Security Note:
 * - Channel identifiers are from the external payment provider; treat as identifiers only.
 * - Do not log or display full reason messages in client-side UI if they contain sensitive data.
 * - Store webhook responses for audit and dispute resolution.
 */
public class AutoPurchaseResponse {
	private double amount;
	private Currency currency;
	private String srcChannel;
	private String destChannel;
	private String reason;
    private boolean success;
}