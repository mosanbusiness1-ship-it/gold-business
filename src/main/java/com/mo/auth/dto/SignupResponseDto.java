package com.mo.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SignupResponseDto
 *
 * Purpose: Response DTO returned after successful user registration/signup.
 * Combines JWT token (for immediate session start) with newly created user summary.
 * Enables frontend to immediately log in newly registered user without requiring
 * a separate login request.
 *
 * Fields:
 * - `token` (String): JWT token for the newly created user. Valid immediately;
 *   no additional login required.
 * - `expiresIn` (long): token expiration time in milliseconds.
 * - `user` (UserSummaryDto): summary of created user containing id, email, fullName.
 *
 * Frontend Guidance:
 * - On successful signup:
 *   1. Store `token` securely (httpOnly cookie preferred).
 *   2. Display confirmation message showing `user.email` and `user.fullName`.
 *   3. Schedule token refresh timer based on `expiresIn`.
 *   4. Redirect to dashboard or onboarding flow.
 * - Use `user` information to populate initial user profile in UI.
 * - If email verification is required, show separate verification prompt
 *   (include verification URL in email, not in this response).
 * - Enable automatic email confirmation link if implemented.
 * - Show optional profile completion workflow if additional fields needed.
 *
 * Security Note:
 * - Validate email format and uniqueness server-side before account creation.
 * - Hash password before storage; never return password in any response.
 * - Implement CAPTCHA on signup endpoint to prevent bot registration.
 * - Require email confirmation before full account activation (optional but recommended).
 * - Rate-limit signup endpoint (e.g., 5 signup attempts per minute per IP).
 * - Log signup events with timestamp and IP for fraud detection.
 * - Store `token` securely; do not expose in logs or error messages.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response returned after successful user signup")
public class SignupResponseDto {

    @Schema(description = "JWT authentication token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Token expiration time in milliseconds", example = "360000")
    private long expiresIn;

    @Schema(description = "Created user summary information")
    private UserSummaryDto user;
}
