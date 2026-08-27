package com.mo.core.dtos.organisationsDtos;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.mo.core.enums.OrganisationType;
import com.mo.core.enums.OrganisationVisibility;

import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
/**
 * CreateOrganisationResponseDTO
 *
 * Purpose: response returned by the backend after successful organisation creation.
 * Contains the persisted organisation with server-assigned id and timestamps.
 *
 * Key fields (echoes CreateOrganisationDTO plus server data):
 * - `id` (Long): server-assigned organisation id.
 * - `ownerId`, `name`, `type`, `category`, `visibility`, `publicJoin`, etc.: mirrored from request.
 * - `createdAt` / `updatedAt` (LocalDateTime): audit timestamps.
 *
 * Frontend guidance:
 * - Use the returned `id` to construct URLs for the organisation dashboard.
 * - Store `ownerId` and organisation members for role-based access control.
 */
public class CreateOrganisationResponseDTO {

    private Long id;

    @JsonProperty("owner_id")
    private Long ownerId;

    private String name;

    private OrganisationType type;
    
    private String category;

    @JsonProperty("logo_url")
    private String logoUrl;

    private OrganisationVisibility visibility = OrganisationVisibility.PRIVATE;
    
    @JsonProperty("public_join")
    private boolean publicJoin;

    @JsonProperty("requires_approval")
    private boolean requiresApproval;
    
    @JsonProperty("restricted_to_admins_only")
    private boolean restrictedToAdminsOnly;
    
    private boolean verified;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    
}