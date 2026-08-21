package com.mo.core.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.auth.User;
import com.mo.core.enums.Currency;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;


@Data @AllArgsConstructor @NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
/**
 * CreateAuctionDTO
 *
 * Purpose: DTO used by frontend clients to create or update an auction event on
 * the backend. Contains scheduling, budget and need description details.
 *
 * Fields:
 * - `id` (Long): auction id, present on update requests.
 * - `ownerId` (Long): id of the auction owner or creator.
 * - `startedAt` / `endAt` (LocalDateTime): auction timeframe.
 * - `isActived` (boolean): whether the auction is active.
 * - `needName`, `needDescription`: textual need summary and details.
 * - `maxPrice` / `currency`: budget cap for the auction.
 * - `needQuantity` (int): quantity requested.
 *
 * Frontend guidance:
 * - Use ISO-8601 timestamps for date/time fields.
 * - Display `currency` alongside `maxPrice` and use the `type` of auction to
 *   choose the appropriate UI flows.
 */
public class CreateAuctionDTO {
	
private Long id;

//@JsonProperty("owner_id")
private Long ownerId;

//@JsonProperty("started_at")
private LocalDateTime startedAt;

//@JsonProperty("end_at")
private LocalDateTime endAt;

private boolean isActived;

//@JsonProperty("need_name")
private String needName;

//@JsonProperty("max_price")
private BigDecimal maxPrice;

@Schema(description = "ISO 4217 currency code", example = "XAF")
private Currency currency;

//@JsonProperty("need_quantity")
private int needQuantity;

//@JsonProperty("need_escription")
private String needDescription;

}