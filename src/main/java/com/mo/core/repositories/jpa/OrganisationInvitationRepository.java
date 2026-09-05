package com.mo.core.repositories.jpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mo.core.enums.InvitationStatus;
import com.mo.core.model.organisations.OrganisationInvitation;

@Repository
public interface OrganisationInvitationRepository extends JpaRepository<OrganisationInvitation, Long> {

    Optional<OrganisationInvitation> findByToken(String token);

    List<OrganisationInvitation> findByOrganisationIdAndStatus(Long organisationId, InvitationStatus status);

    List<OrganisationInvitation> findByInvitedEmail(String email);

    List<OrganisationInvitation> findByOrganisationIdAndInvitedEmail(Long organisationId, String email);

    boolean existsByTokenAndStatus(String token, InvitationStatus status);

    @Query("SELECT oi FROM OrganisationInvitation oi WHERE oi.status = 'PENDING' AND oi.expiresAt < CURRENT_TIMESTAMP")
    List<OrganisationInvitation> findExpiredInvitations();

    @Query("SELECT COUNT(oi) FROM OrganisationInvitation oi WHERE oi.organisation.id = :orgId AND oi.status = :status")
    long countByOrganisationIdAndStatus(@Param("orgId") Long orgId, @Param("status") InvitationStatus status);

    List<OrganisationInvitation> findByOrganisationIdOrderBySentAtDesc(Long organisationId);

    List<OrganisationInvitation> findByInviterIdOrderBySentAtDesc(Long inviterId);
}
