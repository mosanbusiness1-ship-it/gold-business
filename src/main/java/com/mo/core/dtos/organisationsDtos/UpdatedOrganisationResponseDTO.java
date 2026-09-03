package com.mo.core.dtos.organisationsDtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.enums.CommissionMode;
import com.mo.core.enums.OrganisationStatus;
import com.mo.core.enums.OrganisationType;
import com.mo.core.enums.OrganisationVisibility;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatedOrganisationResponseDTO {

    private Long id;

    private String name;

    private OrganisationType type;
    
    @JsonProperty("logo_url")
    private String logoUrl;

    private String category; // phone, food, pc, fashion, realestate, service, etc.

    private OrganisationVisibility visibility = OrganisationVisibility.PRIVATE; // visibility: PUBLIC / PRIVATE / PROTECTED
    
    @JsonProperty("join_token")
    private String joinToken; // UUID ou token généré pour l’invitation

    @JsonProperty("public_join")
    private boolean publicJoin; // lien accessible publiquement ou non

    @JsonProperty("requires_approval")
    private boolean requiresApproval; // demande d'approbation ou non
    
    @JsonProperty("restricted_to_adminsOnly")
    private boolean restrictedToAdminsOnly;// seul les admins y publient
    
    private boolean verified;

    @JsonProperty("commission_percent")
    private BigDecimal commissionPercent;

    private Integer trustLevel; // e.g., 0-100 or 1-5 depending on UI

    private String location;

    private OrganisationStatus status;

    @JsonProperty("commission_on_publish")
    private BigDecimal commissionOnPublish;

    @JsonProperty("commission_on_sale")
    private BigDecimal commissionOnSale;

    @JsonProperty("commission_mode")
    private CommissionMode commissionMode;

    @JsonProperty("offers_guarantee")
    private boolean offersGuarantee;

    private String city;

    private String country;

    private Double latitude;

    private Double longitude;
    
    @UpdateTimestamp
    @JsonProperty("updated_ad")
    private LocalDateTime updatedAt;
    
}
