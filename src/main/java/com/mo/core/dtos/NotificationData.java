package com.mo.core.dtos;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.dtos.autoPurchase.AutoPurchaseDTO;
import com.mo.core.enums.Currency;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data @AllArgsConstructor @NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
/**
 * NotificationData
 *
 * Purpose: DTO representing notification payloads sent from the backend to the frontend.
 * It carries event-related details such as user contact info, product metadata,
 * matching identifiers, and notification links.
 *
 * Fields include user details, product amount/currency, event type, match counts,
 * ids for products/needs/auctions, and optional details links.
 *
 * Frontend guidance:
 * - Use this object to render notification cards, emails, or push messages.
 * - Use `eventType` and `matchType` to decide how to display each notification.
 */
public class NotificationData {
	
	@JsonProperty("user_name")
	private String userName;
	
	@JsonProperty("user_full_name")
	private String userFullName;
	
	@JsonProperty("user_phone_number")
	private String userPhoneNumber;
	
	@JsonProperty("user_email")
	private String userEmail;
	
	@JsonProperty("product_amount")
	private BigDecimal productAmount;
	
	@JsonProperty("product_currency")
    private Currency productCurrency;
	
	@JsonProperty("product_name")
    private String productName ;
    
    @JsonProperty("product_quantity")
    private int productQuantity;
    
    @JsonProperty("view_details_link")
    private String viewDetailsLink;
    
	@JsonProperty("need_description")
    private String needDescription ;
    
	@JsonProperty("event_type")
	private String eventType;
    
	@JsonProperty("match_type")
	private String matchType;
    
	@JsonProperty("match_count")
	private Integer matchCount;
    
	@JsonProperty("product_id")
	private Long productId;
    
	@JsonProperty("need_id")
	private Long needId;
    
	@JsonProperty("auction_id")
	private Long auctionId;
    
	@JsonProperty("event_timestamp")
	private String eventTimestamp;
    
	@JsonProperty("details")
	private String details;
    
    
    

}