package com.mo.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ErrorResponseDto
 *
 * Purpose: Standardized error response DTO for authentication endpoint failures.
 * Used to communicate validation errors, authentication failures, 2FA issues, or
 * unauthorized access attempts to frontend in a consistent format.
 *
 * Fields:
 * - `error` (String): human-readable error message describing what went wrong
 *   (e.g., "Invalid TOTP code", "Email already exists", "Account locked").
 * - `status` (int): HTTP status code associated with the error (e.g., 400, 401, 409).
 * - `timestamp` (String): timestamp of when the error occurred (ISO 8601 format).
 *   Used for logging, debugging, and audit trails.
 *
 * Frontend Guidance:
 * - Always inspect the `status` code first to determine error category:
 *   - 400: validation error, show user-friendly message from `error` field.
 *   - 401: unauthorized/authentication failed, prompt user to login/retry.
 *   - 409: conflict (email exists), guide user to use different email or recover account.
 *   - 500: server error, display generic "try again later" and log `timestamp` for support.
 * - Display `error` message as user-facing toast or alert (avoid raw technical jargon).
 * - Log error details with timestamp for client-side debugging.
 * - Implement retry logic based on error type: validation errors don't retry,
 *   temporary errors (5xx) retry with backoff.
 *
 * Security Note:
 * - Error messages should not expose sensitive system details (file paths, database info).
 * - Do not replay requests with sensitive data based on error responses.
 * - Rate-limit login attempts to prevent brute force; check error frequency client-side.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error response for authentication failures")
public class ErrorResponseDto {

    @Schema(description = "Error message", example = "Invalid TOTP code")
    private String error;

    @Schema(description = "HTTP status code", example = "401")
    private int status;

    @Schema(description = "Error timestamp", example = "2026-07-20T18:45:11")
    private String timestamp;
}
