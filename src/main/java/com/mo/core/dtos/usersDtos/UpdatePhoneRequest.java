package com.mo.core.dtos.usersDtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
/**
 * UpdatePhoneRequest
 *
 * Purpose: DTO for phone number update requests. Allows users to register or
 * change their contact phone number.
 *
 * Fields:
 * - `phoneNumber` (String): phone number in international format (optional +
 *   prefix) or local format. Must be 7–15 digits per validation regex.
 *
 * Frontend guidance:
 * - Provide input masking to help users enter the phone number in the expected
 *   format (e.g., +1 (234) 567-8901).
 * - Display the validation error if format is incorrect.
 * - Consider offering a phone verification step after update to confirm
 *   ownership of the number.
 */
public class UpdatePhoneRequest {
    @NotBlank
    @Pattern(regexp = "\\+?[0-9]{7,15}", message = "Phone number format invalid")
    
    private String phoneNumber;
}
