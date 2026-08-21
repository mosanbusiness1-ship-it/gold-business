package com.mo.core.dtos.autoPurchase;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor @NoArgsConstructor
/**
 * ConfirmPendingTransferData
 *
 * Purpose: Data transfer object for confirming a pending transfer or pending auto-purchase.
 * Used in escrow workflows where a transaction is held pending user confirmation (e.g., OTP,
 * 2FA, or user review) before final settlement. Consolidates notification preferences and
 * payment link information.
 *
 * Fields:
 * - `notificationChannel` (String): the preferred notification method for sending confirmation link.
 *   Accepted values: "email", "sms", "whatsapp", or other supported channels.
 *   This tells the system how to send the payment/confirmation link to the user.
 * - `payLink` (String): the URL or reference link for the user to confirm and complete the payment.
 *   May redirect to an external gateway (e.g., 3D Secure) or internal confirmation page.
 * - `transactionReason` (String): human-readable reason or description of the pending transaction
 *   (e.g., "Product purchase confirmation", "Escrow settlement", "Fund transfer authorization").
 *   Used in notification messages and transaction logs.
 *
 * Frontend Guidance:
 * - Upon receiving this DTO, display a confirmation screen with:
 *   - Transaction reason and details.
 *   - "Confirm Payment" button linking to `payLink`.
 * - Send confirmation link via indicated `notificationChannel` to user's registered contact.
 * - Validate `notificationChannel` against user's verified contact methods (email, phone, etc.).
 * - If user prefers another channel, allow modification before sending.
 * - Handle payment link expiry: UI should warn if link is older than 15 minutes.
 *
 * Security Note:
 * - Payment links should be time-limited and single-use to prevent replay attacks.
 * - Validate that the user requesting confirmation is the transaction owner.
 * - Log all confirmation link deliveries and accesses for audit trail.
 * - Do not embed sensitive data (e.g., account numbers, amounts) in notification messages.
 */
public class ConfirmPendingTransferData {
	
	@JsonProperty("notification_canal")
    private String notificationChannel;
	
	@JsonProperty("pay_link")
    private String payLink;
	
	@JsonProperty("transaction_reason")
	private String transactionReason;
	
}