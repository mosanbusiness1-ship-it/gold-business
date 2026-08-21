package com.mo.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TwoFaResponseDto
 *
 * Purpose: Response DTO indicating that two-factor authentication (2FA) is required
 * before login can complete. Returned when user's password is valid but 2FA is enabled.
 * Frontend uses `userId` to make subsequent 2FA verification request with OTP code.
 *
 * Fields:
 * - `message` (String): confirmation message indicating 2FA is required
 *   (e.g., "2FA required", "Veuillez confirmer avec votre code 2FA").
 * - `userId` (Long): unique identifier of the authenticated user (before 2FA).
 *   Frontend uses this to link 2FA verification request to correct user.
 *
 * Frontend Guidance:
 * - Display 2FA entry form when receiving this response:
 *   1. Show message to user: "Enter the code from your authenticator app".
 *   2. Clear password from form if visible.
 *   3. Display OTP input field (typically 6 digits).
 * - On user submitting OTP:
 *   1. Send request with `userId` and OTP code to 2FA verification endpoint.
 *   2. If verification succeeds, backend returns JWT token (complete login).
 *   3. If verification fails, show error and allow retry (max 3-5 attempts).
 * - Implement timer for OTP expiry (typically 30 seconds); warn user before expiry.
 * - Provide "Didn't receive code?" option to resend OTP.
 * - Allow backup codes option if user lost authenticator device.
 *
 * Security Note:
 * - 2FA adds second authentication factor; never skip it if enabled for user.
 * - Hash and verify OTP codes server-side; never transmit unencrypted.
 * - Rate-limit 2FA verification attempts (max 3 failed attempts, then 15-min lockout).
 * - Log failed 2FA attempts for fraud detection.
 * - Do not reveal if user account exists based on 2FA requirement (timing attack prevention).
 * - OTP codes should be time-based (TOTP) or sequence-based (HOTP); validate on server.
 * - Support backup codes for account recovery if authenticator is lost.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response indicating 2FA is required for this user")
public class TwoFaResponseDto {

    @Schema(description = "Message indicating 2FA is required", example = "2FA required")
    private String message;

    @Schema(description = "User ID to use in 2FA verification request", example = "1")
    private Long userId;
}
