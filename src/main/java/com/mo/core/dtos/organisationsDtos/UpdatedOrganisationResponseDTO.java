package com.mo.core.dtos.organisationsDtos;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.auth.User;
import com.mo.core.enums.OrganisationType;
import com.mo.core.enums.OrganisationVisibility;
import com.mo.core.model.organisations.Organisation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatedOrganisationResponseDTO {
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
