package com.mo.core.model.organisations;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.mo.auth.User;
import com.mo.core.enums.MemberStatus;
import com.mo.core.enums.MemberType;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "organisation_members")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganisationMember {

    @EmbeddedId
    private OrganisationMemberId id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("organisationId")
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberType type;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    private LocalDateTime joinedAt;
    private LocalDateTime modifiedAt;

    // Rôles spécifiques au sein de l'organisation
    @ElementCollection
    @CollectionTable(name = "member_roles", joinColumns = {
        @JoinColumn(name = "organisation_id", referencedColumnName = "organisation_id"),
        @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    })
    @Column(name = "role")
    @Builder.Default
    private Set<String> roles = new HashSet<>();

    // ---------------------- Ajouts -------------------------

 // Utilisateur qui a invité le membre (inviteur)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_user_id")
    private User inviter;

    // Utilisateur qui a approuvé (validé) la demande d’adhésion
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_user_id")
    private User approver;

    private LocalDateTime approvedAt;

}






