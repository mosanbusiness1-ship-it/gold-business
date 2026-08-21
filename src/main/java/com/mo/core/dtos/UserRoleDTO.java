package com.mo.core.dtos;

import lombok.Data;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
/**
 * UserRoleDTO
 *
 * Purpose: DTO representing a user-role association for permission management.
 *
 * Fields:
 * - `userId` (Long): id of the user.
 * - `roleId` (Long): id of the role assigned to the user.
 *
 * Frontend guidance:
 * - Use this DTO when assigning roles or managing access rights.
 */
public class UserRoleDTO {
	
	private Long userId;
	private Long roleId;

}