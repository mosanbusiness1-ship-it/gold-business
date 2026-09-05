package com.mo.core.model.organisations;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mo.auth.User;
import com.mo.core.enums.InvitationStatus;
import com.mo.core.enums.MemberType;

import java.time.LocalDateTime;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "organisation_invitations",
       indexes = {
           @Index(name = "idx_org_inv_organisation", columnList = "organisation_id"),
           @Index(name = "idx_org_inv_inviter", columnList = "inviter_id"),
           @Index(name = "idx_org_inv_email", columnList = "invited_email"),
           @Index(name = "idx_org_inv_token", columnList = "token", unique = true),
           @Index(name = "idx_org_inv_status", columnList = "status"),
           @Index(name = "idx_org_inv_expires_at", columnList = "expires_at")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrganisationInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id", nullable = false)
    private User inviter;

    @Column(nullable = false)
    private String invitedEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberType role;

    @Column(nullable = false, unique = true)
    private String token;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime sentAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column
    private LocalDateTime acceptedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column
    private LocalDateTime revokedAt;
}
