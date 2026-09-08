package com.mo.configuration;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.mo.auth.User;
import com.mo.core.enums.MemberType;
import com.mo.core.enums.OrganisationVisibility;
import com.mo.core.model.organisations.Organisation;
import com.mo.core.services.OrganisationMembershipService;
import com.mo.core.services.OrganisationService;
import com.mo.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class OrganisationSecurity {

    private static final Logger log = LoggerFactory.getLogger(OrganisationSecurity.class);

    private final OrganisationMembershipService membershipService;
    private final OrganisationService organisationService;
    private final UserRepository userRepository;

    public OrganisationSecurity(OrganisationMembershipService membershipService, UserRepository userRepository, OrganisationService organisationService) {
        this.membershipService = membershipService;
		this.organisationService = organisationService;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser(Authentication authentication) {
        String email = authentication.getName();
        log.debug("OrganisationSecurity.getAuthenticatedUser: authenticationName={}, email={}", authentication != null ? authentication.getName() : null, email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));
    }

    private boolean hasOrgPermission(Authentication authentication, Long organisationId, Set<String> allowedRoles) {
        User user = getAuthenticatedUser(authentication);
        log.debug("OrganisationSecurity.hasOrgPermission: userId={}, organisationId={}, allowedRoles={}", user.getId(), organisationId, allowedRoles);
        if (!membershipService.isMember(user.getId(), organisationId)) {
            log.warn("OrganisationSecurity.hasOrgPermission: userId={} not member of organisationId={}", user.getId(), organisationId);
            return false;
        }

        MemberType memberType = membershipService.findActiveMemberType(user.getId(), organisationId).orElse(null);
        log.debug("OrganisationSecurity.hasOrgPermission: userId={}, orgId={}, memberType={}", user.getId(), organisationId, memberType);
        if (memberType != null && allowedRoles.contains(memberType.name())) {
            return true;
        }

        return allowedRoles.stream().anyMatch(role -> membershipService.isMemberWithRole(user.getId(), organisationId, role));
    }

    public boolean isAdminOfOrganisation(Authentication authentication, Long organisationId) {
        User user = getAuthenticatedUser(authentication);
        Optional<MemberType> activeType = membershipService.findActiveMemberType(user.getId(), organisationId);
        boolean isOwner = activeType.filter(type -> type == MemberType.OWNER).isPresent();
        boolean isAdmin = activeType.filter(type -> type == MemberType.ADMIN).isPresent();
        log.debug("OrganisationSecurity.isAdminOfOrganisation: userId={}, organisationId={}, activeType={}, isOwner={}, isAdmin={}", user.getId(), organisationId, activeType.orElse(null), isOwner, isAdmin);
        return isOwner || isAdmin;
    }

    public boolean isModeratorOfOrganisation(Authentication authentication, Long organisationId) {
        User user = getAuthenticatedUser(authentication);
        MemberType activeType = membershipService.findActiveMemberType(user.getId(), organisationId).orElse(null);
        boolean isModerator = activeType == MemberType.ADMIN || activeType == MemberType.OWNER;
        log.debug("OrganisationSecurity.isModeratorOfOrganisation: userId={}, organisationId={}, activeType={}, isModerator={}", user.getId(), organisationId, activeType, isModerator);
        return isModerator;
    }

    public boolean canModerateOrganisation(Authentication authentication, Long organisationId) {
        User user = getAuthenticatedUser(authentication);
        MemberType activeType = membershipService.findActiveMemberType(user.getId(), organisationId).orElse(null);
        boolean canModerate = activeType == MemberType.ADMIN || activeType == MemberType.OWNER;
        log.debug("OrganisationSecurity.canModerateOrganisation: userId={}, organisationId={}, activeType={}, canModerate={}", user.getId(), organisationId, activeType, canModerate);
        return canModerate;
    }

    public boolean isMemberOfOrganisation(Authentication authentication, Long organisationId) {
        User user = getAuthenticatedUser(authentication);
        return membershipService.isMember(user.getId(), organisationId);
    }

    public boolean hasAnyRoleInOrganisation(Authentication authentication, Long organisationId, List<String> roles) {
        User user = getAuthenticatedUser(authentication);
        return roles.stream()
                .anyMatch(role -> membershipService.isMemberWithRole(user.getId(), organisationId, role));
    }

    public boolean isOwnerOrAdmin(Authentication authentication, Long organisationId) {
        User user = getAuthenticatedUser(authentication);
        MemberType activeType = membershipService.findActiveMemberType(user.getId(), organisationId).orElse(null);
        boolean isOwnerOrAdmin = activeType == MemberType.OWNER || activeType == MemberType.ADMIN;
        log.debug("OrganisationSecurity.isOwnerOrAdmin: userId={}, organisationId={}, activeType={}, isOwnerOrAdmin={}", user.getId(), organisationId, activeType, isOwnerOrAdmin);
        return isOwnerOrAdmin;
    }

    public boolean isAllowedToAddProduct(Authentication authentication, Long organisationId) {
        User user = getAuthenticatedUser(authentication);
        Organisation org = organisationService.getOrganisationById(organisationId)
                .orElseThrow(() -> new EntityNotFoundException("Organisation introuvable"));

        if (org.getVisibility() == OrganisationVisibility.PUBLIC) {
            if (org.isRestrictedToAdminsOnly()) {
                return isAdminOfOrganisation(authentication, organisationId);
            }
            return hasOrgPermission(authentication, organisationId, Set.of("ADMIN", "OWNER", "SELLER"));
        }

        if (!membershipService.isMember(user.getId(), organisationId)) {
            return false;
        }

        if (org.isRestrictedToAdminsOnly()) {
            return isAdminOfOrganisation(authentication, organisationId);
        }

        return hasOrgPermission(authentication, organisationId, Set.of("ADMIN", "OWNER", "SELLER"));
    }

    public boolean canPublishNeed(Authentication authentication, Long organisationId) {
        User user = getAuthenticatedUser(authentication);
        Organisation org = organisationService.getOrganisationById(organisationId)
                .orElseThrow(() -> new EntityNotFoundException("Organisation introuvable"));

        if (org.getVisibility() == OrganisationVisibility.PUBLIC) {
            return !org.isRestrictedToAdminsOnly() || isAdminOfOrganisation(authentication, organisationId);
        }

        if (!membershipService.isMember(user.getId(), organisationId)) {
            return false;
        }

        return !org.isRestrictedToAdminsOnly() || isAdminOfOrganisation(authentication, organisationId);
    }
    
    public boolean canAccessProducts(Authentication authentication, Long organisationId) {
        Organisation org = organisationService.getOrganisationById(organisationId)
            .orElseThrow(() -> new EntityNotFoundException("Organisation introuvable"));

        if (org.getVisibility() == OrganisationVisibility.PUBLIC) {
            return true;
        }

        User user = getAuthenticatedUser(authentication);
        return membershipService.isMember(user.getId(), organisationId);
    }

}

