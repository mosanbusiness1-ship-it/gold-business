package com.mo.core.dtos.organisationsDtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.mo.core.enums.OrganisationType;
import com.mo.core.enums.OrganisationVisibility;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
/**
 * CreateOrganisationDTO
 *
 * Purpose: DTO used by the frontend to create a new organisation on the platform.
 *
 * Important fields:
 * - `ownerId` (Long): id of the user who owns/creates the organisation.
 * - `name` (String): organisation name for display.
 * - `type` (OrganisationType): classification (e.g., SELLER, BUYER, SERVICE_PROVIDER).
 * - `category` (String): industry/vertical category (free-form).
 * - `visibility` (OrganisationVisibility): visibility of the organisation (PUBLIC / PRIVATE / PROTECTED).
 * - `publicJoin` (boolean): whether non-admins can join freely.
 * - `requiresApproval` (boolean): whether membership requires approval.
 * - `restrictedToAdminsOnly` (boolean): whether only admins can manage content.
 *
 * Frontend guidance:
 * - Validate organisation name is unique and non-empty before sending.
 * - Use `type`, `category`, and `visibility` to show appropriate fields and workflows.
 * - Display membership policies (`publicJoin`, `requiresApproval`) in summary for transparency.
 */
public class CreateOrganisationDTO {
	
	@JsonProperty("owner_id")
	private Long ownerId;

    private String name;

    private OrganisationType type;
    
    private String category; // free-form category (industry/vertical)

    private OrganisationVisibility visibility = OrganisationVisibility.PRIVATE;
    
    @JsonProperty("public_join")
    private boolean publicJoin;

    @JsonProperty("requires_approval")
    private boolean requiresApproval;
    
    @JsonProperty("restricted_to_admins_only")
    private boolean restrictedToAdminsOnly;
    
}