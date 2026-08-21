package com.mo.core.dtos.autoPurchase;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.enums.Currency;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
/**
 * AutoPurchaseNotificationDataDTO
 *
 * Purpose: Data transfer object containing notification data for auto-purchase transaction completion.
 * Used to send transaction confirmation details to buyer and seller via email/SMS when an
 * auto-purchase is completed or fails. This DTO aggregates transaction and contact information
 * for notification dispatch via Kafka producers.
 *
 * Fields:
 * - `productId` (Long): unique identifier of the purchased product.
 * - `productName` (String): human-readable product name for notification content.
 * - `productQuantity` (int): quantity of the product purchased.
 * - `ownerEmail` (String): email address of the product owner (seller).
 * - `ownerPhoneNumber` (String): phone number of the product owner for SMS notification.
 * - `userEmail` (String): email address of the buyer.
 * - `userphoneNumber` (String): phone number of the buyer for SMS notification.
 * - `success` (boolean): flag indicating transaction success (true) or failure (false).
 * - `amount` (BigDecimal): total transaction amount.
 * - `currency` (Currency enum): currency type of the transaction.
 *
 * Frontend Guidance:
 * - Frontend does not directly construct this DTO; it receives notification data as response.
 * - Display transaction confirmations with product details, amount, and status (success/failed).
 * - Show appropriate messaging based on `success` flag: confirmation on success, error details on failure.
 * - Enable resend of notification functionality if needed by user request.
 *
 * Security Note:
 * - Email and phone numbers are from verified user profiles; treat as contact info only.
 * - Do not expose full transaction details until user logs in and verifies ownership.
 */
public class AutoPurchaseNotificationDataDTO {
	
	//Elements à envoyer pour la notification de l'utilisateur
	@JsonProperty("product_id")
    private Long productId ;
    
    @JsonProperty("product_name")
    private String productName ;
    
    @JsonProperty("product_quantity")
    private int productQuantity;
    
    @JsonProperty("owner_email")
    private String ownerEmail;
    
    @JsonProperty("owner_phone_number")
    private String ownerPhoneNumber;
    
    @JsonProperty("user_email")
    private String userEmail;
    
    @JsonProperty("user_phone_number")
    private String userphoneNumber;
    
    private boolean success;
    private BigDecimal amount;
    private Currency currency;
    

}