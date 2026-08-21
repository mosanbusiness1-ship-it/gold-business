package com.mo.core.dtos;

import java.util.Set;

import com.mo.core.enums.MemberType;

import lombok.Data;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
/**
 * AddMemberRequest
 *
 * Purpose: request DTO used by frontend clients to add a member to an organisation.
 *
 * Fields:
 * - `userId` (Long): id of the user to add.
 * - `type` (MemberType): membership role or classification within the organisation.
 * - `roles` (Set<String>): optional additional role identifiers.
 *
 * Frontend guidance:
 * - Use in organisation admin flows when assigning new members.
 * - Ensure the user has appropriate permissions to add members before sending.
 */
public class AddMemberRequest {
    private Long userId;
    private MemberType type;
    private Set<String> roles;
}