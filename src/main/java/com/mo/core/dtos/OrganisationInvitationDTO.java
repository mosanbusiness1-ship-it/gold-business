package com.mo.core.dtos;

import java.time.LocalDateTime;

import com.mo.core.enums.InvitationStatus;
import com.mo.core.enums.MemberType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganisationInvitationDTO {

    private Long id;
    private Long organisationId;
    private Long inviterId;
    private String invitedEmail;
    private MemberType role;
    private String token;
    private String invitationLink;
    private LocalDateTime sentAt;
    private LocalDateTime expiresAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime revokedAt;
    private InvitationStatus status;
}
