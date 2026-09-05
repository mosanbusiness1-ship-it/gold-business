package com.mo.core.dtos;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
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
    private String inviterName;
    private String inviterEmail;
}
