package com.mo.core.dtos;

import java.util.List;
import java.util.Map;

import com.mo.core.model.needs.AbstractUserNeed;
import com.mo.core.model.products.AbstractProduct;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data @AllArgsConstructor @NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
/**
 * ProductAndMatchedNeedsDTO
 *
 * Purpose: response DTO returned when the backend pairs a product with matching needs.
 *
 * Fields:
 * - `product` (AbstractProduct): the product involved in the match.
 * - `needs` (List<Map<String,Object>>): list of need payloads matched to the product.
 *
 * Frontend guidance:
 * - Use this DTO in search results or recommendation flows that display matches.
 * - Render product details alongside matching need summaries.
 */
public class ProductAndMatchedNeedsDTO {
	
	private AbstractProduct product;
	private  List<Map<String, Object>> needs;

}