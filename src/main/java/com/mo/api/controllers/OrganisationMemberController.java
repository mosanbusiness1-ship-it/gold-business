package com.mo.api.controllers;

import java.util.List;
import java.util.Base64;
import java.util.Map;

import java.util.Set;
import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;

import com.mo.auth.JwtService;
import com.mo.auth.User;
import com.mo.core.dtos.AddMemberRequest;
import com.mo.core.enums.MemberType;
import com.mo.core.enums.OrganisationType;
import com.mo.core.model.organisations.Organisation;
import com.mo.core.model.organisations.OrganisationMember;
import com.mo.core.services.OrganisationMembershipService;
import com.mo.core.services.OrganisationService;
import com.mo.core.services.QrCodeGeneratorService;
import com.mo.core.enums.OrganisationVisibility;

import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orgmembers")
@RequiredArgsConstructor
public class OrganisationMemberController {

    private final OrganisationMembershipService membershipService;
    private final OrganisationService organisationService;
    private final QrCodeGeneratorService qrCodeGeneratorService;
    private JwtService jwtService;

    @PostMapping("/{orgId}/members")
    @Operation(summary = "Add organisation member", description = "Add a user to the organisation with the requested member type and roles")
    public ResponseEntity<OrganisationMember> addMember(
            @PathVariable Long orgId,
            @RequestBody AddMemberRequest request) {
        OrganisationMember membership = membershipService.addMemberToOrganisation(
            orgId,
            request.getUserId(),
            request.getType(),
            request.getRoles()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(membership);
    }
    
    @PutMapping("/{orgId}/members")
    @Operation(summary = "Update member role", description = "Update a member's type in the organisation")
    public ResponseEntity<OrganisationMember> setMemberRole(
            @PathVariable Long orgId,
            @RequestBody AddMemberRequest request) {
        OrganisationMember membership = membershipService.updateMemberType(
            orgId,
            request.getUserId(),
            request.getType()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(membership);
    }
    
    
    @GetMapping("/{orgId}/members/admins")
    @Operation(summary = "Get organisation admins", description = "Return all administrators for the organisation")
    public ResponseEntity<List<User>> getAdmins(@PathVariable Long orgId) {
        return ResponseEntity.ok(membershipService.getOrganisationAdmins(orgId));
    }
    
    @GetMapping("/{orgId}/members")
    @Operation(summary = "Get organisation members", description = "Return the full member list for the organisation")
    public ResponseEntity<List<User>> getMembers(@PathVariable Long orgId) {
        return ResponseEntity.ok(membershipService.getOrganisationFullMembers(orgId));
    }
    
    @GetMapping("/{userId}/community")
    @Operation(summary = "Get user community organisations", description = "Return sales community organisations for the given user")
    public ResponseEntity<List<Organisation>> getUserCommunity(@PathVariable Long userId) {
        return ResponseEntity.ok(membershipService.getUserGroupOrganisation(userId, OrganisationType.COMMUNITY));
    }
    
    @GetMapping("/{userId}/group")
    @Operation(summary = "Get user group organisations", description = "Return sales group organisations for the given user")
    public ResponseEntity<List<Organisation>> getUserGroup(@PathVariable Long userId) {
        return ResponseEntity.ok(membershipService.getUserGroupOrganisation(userId, OrganisationType.GROUP));
    }
    
    

    // donner un nouveau role à un membre
    @PatchMapping("/{orgId}/members/{userId}/type")
    @Operation(summary = "Patch member type", description = "Update the member type for a specific user in the organisation")
    public ResponseEntity<Void> updateMemberType(
            @PathVariable Long orgId,
            @PathVariable Long userId,
            @RequestParam MemberType type) {
        membershipService.updateMemberType(orgId, userId, type);
        return ResponseEntity.noContent().build();
    }
    

    // genere le lien d'invitation
    @PostMapping("/{orgId}/invite")
    @Operation(summary = "Generate invitation link", description = "Generate an invitation link for a user to join the organisation")
    public ResponseEntity<String> generateInvitationLink(
            @PathVariable("orgId") Long organisationId,
            @RequestParam String email,
            @RequestAttribute("userId") Long inviterUserId) {

        String token = organisationService.generateInvitationToken(organisationId, inviterUserId, email);
        String invitationLink = "https://tonapp.com/invitations/accept?token=" + token;

        return ResponseEntity.ok(invitationLink);
    }
    
    
    @GetMapping("/{orgId}/invite/qr")
    @Operation(summary = "Generate invitation QR code", description = "Generate an invitation QR code for joining the organisation")
    public ResponseEntity<Map<String, String>> generateInvitationQrCode(
            @PathVariable("orgId") Long organisationId,
            @RequestParam String email,
            @RequestAttribute("userId") Long inviterUserId) throws Exception {

        String token = organisationService.generateInvitationToken(organisationId, inviterUserId, email);
        String invitationLink = "https://tonapp.com/invitations/accept?token=" + token;

        // Générer le QR code à partir du lien
        byte[] qrCodeBytes = qrCodeGeneratorService.generateQrCode(invitationLink, 300, 300);
        String base64QrCode = Base64.getEncoder().encodeToString(qrCodeBytes);

        return ResponseEntity.ok(Map.of(
            "base64QrCode", base64QrCode,
            "invitationLink", invitationLink
        ));
    }

    
    // joindre une organisation public ou demander une adhesion à une org privée
    @PostMapping("/join")
    @Operation(summary = "Join organisation", description = "Join an organisation either by invitation token (private) or by organisationId for public/protected orgs")
    public ResponseEntity<?> joinOrganisation(
            @RequestParam(required = false) String token,
            @RequestParam(required = false) Long organisationId,
            @RequestAttribute("userId") Long userId) {

        // If a token is provided, treat this as an invitation acceptance (used for PRIVATE orgs)
        if (token != null && !token.isBlank()) {
            try {
                organisationService.acceptInvitationToken(token, userId);
                return ResponseEntity.ok("Invitation acceptée. Vous avez rejoint l'organisation.");
            } catch (IllegalArgumentException | EntityNotFoundException | IllegalStateException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors du traitement de l'invitation.");
            }
        }

        // Otherwise require an organisationId to join public/protected orgs
        if (organisationId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Provide either an invitation token or organisationId.");
        }

        Organisation org = organisationService.findById(organisationId);

        // Only PUBLIC organisations can be joined without an invitation.
        // PROTECTED and PRIVATE require an invitation/token.
        if (org.getVisibility() == OrganisationVisibility.PRIVATE || org.getVisibility() == OrganisationVisibility.PROTECTED) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Joining this organisation requires an invitation token.");
        }

        if (membershipService.isMember(userId, organisationId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Already a member.");
        }

        if (org.isRequiresApproval()) {
            membershipService.createPendingRequest(userId, organisationId);
            return ResponseEntity.ok("Join request submitted for approval.");
        }

        // Directly add member for public or protected orgs (no token)
        membershipService.addMemberToOrganisation(organisationId, userId, MemberType.FULL_MEMBER, Set.of());
        return ResponseEntity.ok("You have joined the organisation.");
    }

    
    
    // approuver un adhesion par un ADMIN
    @PutMapping("/{organisationId}/approve")
    @PreAuthorize("@organisationSecurity.isAdminOfOrganisation(authentication, #organisationId)")
    @Operation(summary = "Approve join request", description = "Approve a pending organisation join request")
    public ResponseEntity<?> approveJoinRequest(
            @PathVariable Long organisationId,
            @RequestParam Long userId
    ) {
        boolean approved = membershipService.approvePendingRequest(userId, organisationId);
        
        if (approved) {
            return ResponseEntity.ok("User has been approved and added to the organisation.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("Pending request not found or already approved.");
        }
    }
    

    
    // accepter une invitation consiste à integrer l'organisation directement sans besoin d'aprobation
    @PostMapping("/accept-invitation")
    @Operation(summary = "Accept invitation", description = "Accept an invitation token and join the organisation")
    public ResponseEntity<?> acceptInvitation(@RequestParam String token, @RequestAttribute("userId") Long userId) {
        organisationService.acceptInvitationToken(token, userId);
        return ResponseEntity.ok("Invitation acceptée. Vous avez rejoint l'organisation.");
    }

    // rejeter une demande d'adhésion
    @PostMapping("/memberships/{membershipId}/reject")
    @Operation(summary = "Reject membership request", description = "Reject a pending membership request for an organisation")
    public ResponseEntity<?> rejectMembershipRequest(@PathVariable Long membershipId, @RequestAttribute("userId") Long approverId) {
        membershipService.rejectRequest(membershipId, approverId);
        return ResponseEntity.ok("Demande d'adhésion rejetée.");
    }


    


}
