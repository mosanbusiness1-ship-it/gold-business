package com.mo.core.repositories.jpa;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mo.core.dtos.GetOrganisationMemberResponseDTO;
import com.mo.core.enums.MemberStatus;
import com.mo.core.enums.MemberType;
import com.mo.core.enums.OrganisationType;
import com.mo.core.model.organisations.OrganisationMember;
import com.mo.core.model.organisations.OrganisationMemberId;

@Repository
public interface OrganisationMemberRepository extends JpaRepository<OrganisationMember, OrganisationMemberId> {

    // Méthode personnalisée
    @Query("SELECT om FROM OrganisationMember om WHERE om.id = :membershipId")
    Optional<OrganisationMember> findMemberById(@Param("membershipId") Long membershipId);

    Optional<OrganisationMember> findByOrganisationIdAndUserId(Long orgId, Long userId);

    List<OrganisationMember> findByOrganisationIdAndType(Long orgId, MemberType type);

    @Query("SELECT om FROM OrganisationMember om WHERE om.organisation.id = :orgId")
    List<OrganisationMember> findByOrganisationId(@Param("orgId") Long orgId);


    @Query("SELECT om FROM OrganisationMember om WHERE om.user.id = :userId AND om.organisation.type = :orgType AND om.status = :status")
    List<OrganisationMember> findByUserIdAndOrganisationTypeAndStatus(
        @Param("userId") Long userId,
        @Param("orgType") OrganisationType orgType,
        @Param("status") MemberStatus status
    );

    List<OrganisationMember> findByUserId(Long userId);

    List<OrganisationMember> findByUserIdAndStatus(Long userId, MemberStatus status);

    boolean existsByOrganisationIdAndUserId(Long organisationId, Long userId);

    boolean existsByOrganisationIdAndUserIdAndStatus(Long organisationId, Long userId, MemberStatus status);
    
    Optional<OrganisationMember> findByOrganisationIdAndUserIdAndStatus(Long organisationId, Long userId, MemberStatus status);

	boolean existsByUserIdAndOrganisationIdAndRoles(Long userId, Long organisationId, String role);
	

}


