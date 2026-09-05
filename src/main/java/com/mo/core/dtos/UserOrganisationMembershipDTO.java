package com.mo.core.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.enums.MemberType;
import com.mo.core.enums.OrganisationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserOrganisationMembershipDTO {
    @JsonProperty("organisation_id")
    private Long organisationId;

    @JsonProperty("organisation_name")
    private String organisationName;

    @JsonProperty("organisation_type")
    private OrganisationType organisationType;

    @JsonProperty("member_type")
    private MemberType memberType;
}
