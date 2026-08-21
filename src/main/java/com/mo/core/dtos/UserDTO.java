package com.mo.core.dtos;

import java.util.Date;
import java.util.List;
import com.mo.auth.Role;

import lombok.Data;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
/**
 * UserDTO
 *
 * Purpose: DTO representing user profile information exchanged between frontend and backend.
 *
 * Fields:
 * - `id` (Long): user id.
 * - `fullName` (String): display name for the user.
 * - `email` (String): email address.
 * - `password` (String): only used during authentication or registration flows.
 * - `phoneNumber` (String): contact phone number.
 * - `roles` (List<Role>): assigned user roles for permissions.
 * - `encryptedSecret` (String): backend-managed secret for MFA or TOTP.
 * - `totpEnabled` (boolean): whether two-factor authentication is enabled.
 *
 * Frontend guidance:
 * - Never render `password` or `encryptedSecret` in client views.
 * - Use `roles` to apply feature access rules.
 */
public class UserDTO {
	
	    private Long id;

	    private String fullName;

	    private String email;

	    private String password;

	    private String phoneNumber;

	    private List<Role> roles;

	    private String encryptedSecret;

	    private boolean totpEnabled;

}