package com.mo.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UserSummaryDto
 *
 * Purpose: Lightweight DTO representing essential user profile information.
 * Returned in authentication responses (signup, login with user context, profile endpoints).
 * Contains only non-sensitive, client-displayable user metadata for UI rendering.
 *
 * Fields:
 * - `id` (Long): unique user identifier in the system.
 *   Used in subsequent API requests to identify the user (e.g., update profile, fetch history).
 * - `email` (String): user's verified email address.
 *   Serves as unique identifier and primary contact method.
 * - `fullName` (String): user's full name as provided during signup.
 *   Display in greeting, profile views, and transaction notifications.
 *
 * Frontend Guidance:
 * - Use `id` to tag all user-specific requests and identify sessions.
 * - Display `email` as account identifier in settings, profile, and notifications.
 * - Show `fullName` in welcome message: "Welcome, [fullName]!".
 * - Use in notification payloads: mention user by name in transaction/order emails.
 * - Store locally in user session context for quick access without re-fetching.
 * - On logout, clear this cached user data.
 * - Enable user to update fullName (non-sensitive) via profile edit endpoint;
 *   this DTO should reflect updates after save.
 *
 * Security Note:
 * - This DTO excludes sensitive fields: password, phone number, wallet ID, internal flags.
 * - Only non-sensitive profile data included; do not add sensitive fields.
 * - Email is verified (per registration/OAuth flow); can be used for contact.
 * - fullName is user-provided text; sanitize before displaying to prevent XSS.
 * - Do not use any fields from this DTO for authorization decisions;
 *   always verify permissions server-side.
 * - Cache this user summary client-side only for UI display; re-fetch on logout.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User summary information")
public class UserSummaryDto {

    @Schema(description = "Unique user identifier", example = "1")
    private Long id;

    @Schema(description = "User email address", example = "user@example.com")
    private String email;

    @Schema(description = "User full name", example = "John Doe")
    private String fullName;
}
