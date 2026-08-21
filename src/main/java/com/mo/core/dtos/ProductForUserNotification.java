package com.mo.core.dtos;

import java.util.List;
import java.util.Map;

import com.mo.core.model.products.AbstractProduct;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data @AllArgsConstructor @NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
/**
 * ProductForUserNotification
 *
 * Purpose: DTO used by the backend to build notifications related to a specific product.
 *
 * Fields:
 * - `userId` (Long): id of the notification recipient.
 * - `product` (AbstractProduct): the product referenced by the notification.
 *
 * Frontend guidance:
 * - Use this object to populate notification messages or activity streams.
 */
public class ProductForUserNotification {
	
	private Long userId;
	private AbstractProduct product;

}