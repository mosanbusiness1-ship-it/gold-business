package com.mo.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ResourceAccessResponseDto
 *
 * Purpose: Simple response DTO confirming successful access to a protected resource.
 * Indicates that the user's JWT token was valid and authentication passed.
 * Used as a generic success response for protected endpoints that don't return
 * domain-specific data (e.g., health checks, permission verification endpoints).
 *
 * Fields:
 * - `message` (String): confirmation message indicating authenticated access.
 *   Example formats: "Vous êtes authentifié : user@example.com" or
 *   "Access granted to [resource name]".
 *   Primarily informational; used for logging and UI feedback.
 *
 * Frontend Guidance:
 * - This response indicates successful JWT validation and user authorization.
 * - Use this DTO to confirm authentication status in tests or debugging.
 * - If received, user's token is valid and can continue executing authenticated operations.
 * - Display message as informational confirmation (optional in production UI).
 * - Log message for audit trail if implementing activity logging.
 * - Use in health check endpoints to verify API is operational with valid auth.
 *
 * Security Note:
 * - This response is only reached if JWT token is valid and signature verified.
 * - Do not assume authorization to other resources; each endpoint has its own permissions.
 * - Token validation happens on every authenticated request by Spring Security filter.
 * - Message may contain user identifier; avoid logging in unsecured environments.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response from protected resource endpoint")
public class ResourceAccessResponseDto {

    @Schema(description = "Message confirming user is authenticated", example = "Utilisateur authentifié : user@example.com")
    private String message;
}
