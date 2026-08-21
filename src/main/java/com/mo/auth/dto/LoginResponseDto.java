package com.mo.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LoginResponseDto
 *
 * Purpose: Response DTO returned after successful user login. Contains JWT token
 * and expiration metadata. Frontend uses this token for all subsequent authenticated
 * requests via Authorization header.
 *
 * Fields:
 * - `token` (String): JWT (JSON Web Token) encoded as a string. Contains user identity
 *   and claims, cryptographically signed by backend. Valid for the duration specified
 *   in `expiresIn`.
 * - `expiresIn` (long): token expiration time in milliseconds from now.
 *   Frontend should refresh or redirect to login when token expires.
 *
 * Frontend Guidance:
 * - Store `token` securely in httpOnly cookie or secure localStorage (prefer httpOnly cookie).
 * - Include token in every authenticated request in Authorization header:
 *   `Authorization: Bearer <token>`
 * - Schedule token refresh before expiration:
 *   - Calculate refresh time = expiresIn * 0.9 (refresh at 90% of lifetime).
 *   - Implement automatic token refresh endpoint call at scheduled time.
 *   - If refresh fails, redirect user to login.
 * - Display login success confirmation and redirect to dashboard.
 * - On token expiry during session, prompt user to re-authenticate.
 * - Do not expose token in logs, error messages, or external API calls.
 *
 * Security Note:
 * - Token is sensitive; never log it or transmit unencrypted.
 * - Store token only in httpOnly cookies (prevents XSS token theft).
 * - Tokens should be invalidated server-side on logout (token blacklist).
 * - Implement CSRF protection for token-based authentication.
 * - Use HTTPS to prevent token interception during transit.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response returned after successful user login")
public class LoginResponseDto {

    @Schema(description = "JWT authentication token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Token expiration time in milliseconds", example = "360000")
    private long expiresIn;
}
