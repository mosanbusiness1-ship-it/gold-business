package com.mo.core.dtos.usersDtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
/**
 * UpdateEmailRequest
 *
 * Purpose: DTO for email update requests. Allows authenticated users to change
 * their registered email address in the system.
 *
 * Fields:
 * - `email` (String): new email address. Must be valid (validated via @Email
 *   annotation) and non-empty.
 *
 * Frontend guidance:
 * - Validate email format client-side before sending to confirm user intent.
 * - Display any error messages from the backend if the email is already
 *   registered or invalid.
 * - Consider sending a confirmation link if the backend implements email
 *   verification on update.
 */
public class UpdateEmailRequest {
    @Email(message = "Email is not valid")
    @NotBlank
    private String email;
}
