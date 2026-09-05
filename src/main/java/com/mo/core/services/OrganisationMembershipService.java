package com.mo.core.services;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mo.auth.User;
import com.mo.core.dtos.GetOrganisationMemberResponseDTO;
import com.mo.core.enums.MemberStatus;
import com.mo.core.enums.MemberType;
import com.mo.core.enums.OrganisationType;
import com.mo.core.model.organisations.Organisation;
import com.mo.core.model.organisations.OrganisationMemberId;
import com.mo.core.model.organisations.OrganisationMember;
import com.mo.core.repositories.jpa.OrganisationMemberRepository;
import com.mo.core.repositories.jpa.OrganisationRepository;
import com.mo.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganisationMembershipService {

    private final OrganisationMemberRepository memberRepository;
    private final OrganisationRepository organisationRepository;
    private final UserRepository userRepository;

    public OrganisationMember addMemberToOrganisation(Long orgId, Long userId, MemberType type, Set<String> roles) {
        Organisation org = organisationRepository.findById(orgId)
            .orElseThrow(() -> new EntityNotFoundException("Organisation not found"));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Créer explicitement l'EmbeddedId
        OrganisationMemberId memberId = new OrganisationMemberId(org.getId(), user.getId());

        OrganisationMember membership = OrganisationMember.builder()
            .id(memberId) // <-- ici on set l'ID !
            .organisation(org)
            .user(user)
            .type(type)
            .roles(roles)
            .status(MemberStatus.ACTIVE)
            .joinedAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now()) // tu peux aussi set modifiedAt
            .build();

        return memberRepository.save(membership);
    }


    public OrganisationMember updateMemberType(Long orgId, Long userId, MemberType newType) {
        OrganisationMember membership = memberRepository.findByOrganisationIdAndUserId(orgId, userId)
            .orElseThrow(() -> new EntityNotFoundException("Membership not found"));
        
        membership.setType(newType);
        return memberRepository.save(membership);
    }

    @Transactional
    public void removeMember(Long orgId, Long userId) {
        OrganisationMember membership = memberRepository.findByOrganisationIdAndUserId(orgId, userId)
            .orElseThrow(() -> new EntityNotFoundException("Membership not found for user " + userId + " in organisation " + orgId));
        
        memberRepository.delete(membership);
    }

    public List<User> getOrganisationAdmins(Long orgId) {
        return memberRepository.findByOrganisationIdAndType(orgId, MemberType.ADMIN)
            .stream()
            .map(OrganisationMember::getUser)
            .collect(Collectors.toList());
    }

     public List<User> getOrganisationFullMembers(Long orgId) {
        return memberRepository.findByOrganisationIdAndType(orgId, MemberType.FULL_MEMBER)
            .stream()
            .map(OrganisationMember::getUser)
            .collect(Collectors.toList());
    }
    
    public List<GetOrganisationMemberResponseDTO> getOrganisationMembers(Long orgId) {
         List<OrganisationMember> orgMembers = memberRepository.findByOrganisationId(orgId);
         orgMembers.forEach(orgMember -> {
             GetOrganisationMemberResponseDTO dto = new GetOrganisationMemberResponseDTO();
             dto.setOrganisationId(orgMember.getOrganisation().getId().toString());
             dto.setUserId(orgMember.getUser().getId().toString());
             dto.setFullName(orgMember.getUser().getFullName());
             dto.setEmail(orgMember.getUser().getEmail());
             dto.setRoles(orgMember.getType());
         });
        return orgMembers.stream()
                .map(orgMember -> new GetOrganisationMemberResponseDTO(
                    orgMember.getOrganisation().getId().toString(),
                    orgMember.getUser().getId().toString(),
                    orgMember.getUser().getFullName(),
                    orgMember.getUser().getEmail(),
                    orgMember.getType()
                ))
                .collect(Collectors.toList());
    }
    
    public List<Organisation> getUserCommunityOrganisation(Long userId, OrganisationType orgType) {
        return memberRepository.findByUserIdAndType(userId, OrganisationType.COMMUNITY)
            .stream()
            .map(OrganisationMember::getOrganisation)
            .collect(Collectors.toList());
    }
    
    public List<Organisation> getUserGroupOrganisation(Long userId, OrganisationType orgType) {
        return memberRepository.findByUserIdAndType(userId, OrganisationType.GROUP)
            .stream()
            .map(OrganisationMember::getOrganisation)
            .collect(Collectors.toList());
    }
    
    public MemberType getMemberType(Long userId, Long organisationId) {
        return memberRepository.findByOrganisationIdAndUserId(organisationId, userId)
            .map(OrganisationMember::getType)
            .orElseThrow(() -> new EntityNotFoundException("Membership not found for user " + userId + " in organisation " + organisationId));
    }

    public Optional<MemberType> findActiveMemberType(Long userId, Long organisationId) {
        return memberRepository.findByOrganisationIdAndUserIdAndStatus(organisationId, userId, MemberStatus.ACTIVE)
            .map(OrganisationMember::getType);
    }
    
    public boolean isMember(Long userId, Long organisationId) {
        return memberRepository.existsByOrganisationIdAndUserIdAndStatus(
            organisationId, userId, MemberStatus.ACTIVE
        );
    }
    
    public OrganisationMember createPendingRequest(Long userId, Long organisationId) {
        // Vérifie si une relation (même en attente ou acceptée) existe déjà
        if (memberRepository.existsByOrganisationIdAndUserId(organisationId, userId)) {
            throw new IllegalStateException("Une demande ou une adhésion existe déjà.");
        }

        Organisation organisation = organisationRepository.findById(organisationId)
            .orElseThrow(() -> new EntityNotFoundException("Organisation introuvable"));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));

        OrganisationMemberId id = new OrganisationMemberId(organisationId, userId);

        OrganisationMember membership = OrganisationMember.builder()
            .id(id)
            .organisation(organisation)
            .user(user)
            .type(null) // ou MemberType.GUEST si tu veux donner un rôle par défaut
    	    .status(MemberStatus.PENDING) // <- statut correct maintenant
            .roles(Collections.emptySet())
            .joinedAt(null) // car non encore accepté
            .modifiedAt(LocalDateTime.now())
            .build();

        return memberRepository.save(membership);
    }
    
    @Transactional
    public void acceptRequest(Long membershipId, Long approverId) {
        OrganisationMember member = memberRepository.findMemberById(membershipId)
            .orElseThrow(() -> new EntityNotFoundException("Demande d'adhésion non trouvée."));

        if (member.getStatus() != MemberStatus.PENDING) {
            throw new IllegalStateException("La demande n’est pas en attente.");
        }

        member.setStatus(MemberStatus.ACTIVE);
        member.setType(MemberType.FULL_MEMBER); // ou autre type par défaut
        member.setJoinedAt(LocalDateTime.now());
        member.setModifiedAt(LocalDateTime.now());

        User approverUser = userRepository.findById(approverId)
            .orElseThrow(() -> new EntityNotFoundException("Approver not found"));
        member.setApprover(approverUser);
        member.setApprovedAt(LocalDateTime.now());

        memberRepository.save(member);
    }

    @Transactional
    public void rejectRequest(Long membershipId, Long approverId) {
        OrganisationMember member = memberRepository.findMemberById(membershipId)
            .orElseThrow(() -> new EntityNotFoundException("Demande d'adhésion non trouvée."));

        if (member.getStatus() != MemberStatus.PENDING) {
            throw new IllegalStateException("La demande n’est pas en attente.");
        }

        // Option 1 : Suppression
        memberRepository.delete(member);

        // Option 2 (alternative) : Mettre le statut REJECTED
        // member.setStatus(MemberStatus.REJECTED);
        // User approverUser = userRepository.findById(approverId)
        //    .orElseThrow(() -> new EntityNotFoundException("Approver not found"));
        // member.setApprover(approverUser);
        // member.setApprovedAt(LocalDateTime.now());
        // memberRepository.save(member);
    }
    
    @Transactional
    public boolean approvePendingRequest(Long userId, Long organisationId) {
        Optional<OrganisationMember> pending = memberRepository
            .findByOrganisationIdAndUserIdAndStatus(organisationId, userId, MemberStatus.PENDING);

        if (pending.isEmpty()) {
            return false;
        }

        OrganisationMember membership = pending.get();
        membership.setStatus(MemberStatus.ACTIVE);
        membership.setType(MemberType.FULL_MEMBER); // facultatif si tu veux promouvoir
        membership.setJoinedAt(LocalDateTime.now());
        membership.setModifiedAt(LocalDateTime.now());

        memberRepository.save(membership);
        return true;
    }


    public boolean isMemberWithRole(Long userId, Long organisationId, String role) {
    	
        return memberRepository.existsByUserIdAndOrganisationIdAndRoles(userId, organisationId, role);
    }
    
//    public boolean isMemberWithRole(Long userId, Long organisationId, String role) {
//        return memberRepository.existsByUserIdAndOrganisationIdAndRole(userId, organisationId, Collections.singleton(role));
//    }


	public Optional<User> getOrganisationById(Long organisationId) {
		// TODO Auto-generated method stub
		return null;
	}

    
}
