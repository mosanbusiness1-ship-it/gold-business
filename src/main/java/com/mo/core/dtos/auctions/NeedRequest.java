package com.mo.core.dtos.auctions;

import java.time.LocalDateTime;

/**
 * NeedRequest
 *
 * Purpose: represents a lightweight need or requirement submitted by a user
 * that can be used to create an auction or to match available products.
 *
 * Important fields:
 * - `ownerId` (Long): id of the user who created the need.
 * - `category` (String): high-level category (maps to ProductOrNeedType enum).
 * - `type` (String): specific product type (maps to ProductType enum).
 * - `title` (String): short title for display in auction listings.
 * - `description` (String): detailed description used by suppliers to bid.
 * - `createdAt` (LocalDateTime): creation timestamp.
 *
 * Frontend guidance:
 * - Use this DTO when a buyer creates a need that should start an auction flow.
 * - Keep `title` concise; `description` may contain structured info (condition, quantity).
 *
 * Example (JSON):
 * { "ownerId": 10, "category": "ELECTRONICS", "type": "PHONE", "title": "Used phone", "description": "Good condition, unlocked" }
 */
public class NeedRequest {
    private Long ownerId;
    private String category; // ProductOrNeedType
    private String type;     // ProductType
    private String title;
    private String description;
    private LocalDateTime createdAt;

    // getters and setters
}