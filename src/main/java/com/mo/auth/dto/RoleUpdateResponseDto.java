package com.mo.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RoleUpdateResponseDto
 *
 * Purpose: Response DTO confirming successful role assignment or removal for a user.
 * Returned after admin/authorized user modifies another user's roles (in role management endpoints).
 * Contains user identification and operation confirmation.
 *
 * Fields:
 * - `userId` (Long): unique identifier of the user whose role was modified.
 * - `email` (String): email address of the user for identification.
 * - `message` (String): confirmation message describing the operation performed
 *   (e.g., "Role ADMIN assigned successfully", "Role USER_SUPPORT removed successfully").
 *
 * Frontend Guidance:
 * - Display operation result on admin panel:
 *   - Show user email and the action performed.
 *   - Display the confirmation message.
 * - Update user role list/table in admin UI after successful operation.
 * - If multiple roles are batched, collect responses and show summary:
 *   - "Successfully updated roles for X users".
 * - Enable undo/rollback functionality by storing previous role state.
 * - Log role changes for audit trail with timestamp and admin who made the change.
 * - On error (status 400/403), show error message and advise checking permissions.
 *
 * Security Note:
 * - Role updates should only be performed by admin/authorized users (enforce server-side).
 * - Do not trust userId or email from request; fetch authenticated admin context.
 * - Log all role changes with admin identity and timestamp for compliance.
 * - Implement rate-limiting on role update endpoints to prevent abuse.
 * - Validate that admin has permission to modify target user's roles.
 * - Do not reveal target user's other sensitive attributes in this response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response after assigning or removing a role to/from user")
public class RoleUpdateResponseDto {

    @Schema(description = "User ID", example = "1")
    private Long userId;

    @Schema(description = "User email", example = "user@example.com")
    private String email;

    @Schema(description = "Operation result message", example = "Role assigned successfully")
    private String message;
}
