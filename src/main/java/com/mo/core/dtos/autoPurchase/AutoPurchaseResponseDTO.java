package com.mo.core.dtos.autoPurchase;

import java.math.BigDecimal;

import com.mo.core.enums.Currency;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
/**
 * AutoPurchaseResponseDTO
 *
 * Purpose: Wrapper DTO for auto-purchase transaction response from backend API to frontend.
 * Combines the original request details with the transaction result status and message,
 * enabling frontend to correlate requests with outcomes and handle errors gracefully.
 *
 * Fields:
 * - `request` (AutoPurchaseDTO): the original auto-purchase request payload that was submitted.
 *   Contains source/destination wallets, product details, quantity, and amount.
 * - `success` (boolean): flag indicating overall transaction success (true) or failure (false).
 * - `message` (String): detailed message or error description from backend:
 *   - On success: confirmation message, reference ID, or settlement timeline.
 *   - On failure: specific error reason (e.g., "Insufficient wallet balance", "Product out of stock",
 *     "Wallet service unavailable").
 *
 * Frontend Guidance:
 * - Upon receiving this response, check `success` flag first:
 *   - If true: acknowledge transaction, display confirmation, update order history.
 *   - If false: display `message` as user-friendly error and prompt retry or support contact.
 * - Preserve `request` details for audit trail and dispute handling.
 * - Implement exponential backoff retry logic on temporary failures.
 * - Use transaction message for detailed logs and user support interactions.
 *
 * Error Handling:
 * - Network timeout: frontend should retry with exponential backoff (3-5 attempts).
 * - Validation errors: review product data and wallet identifiers before retrying.
 * - Gateway errors: display `message` and offer manual transaction alternative.
 */
public class AutoPurchaseResponseDTO {
	
	private AutoPurchaseDTO request;
    private boolean success;
    private String message;

}
