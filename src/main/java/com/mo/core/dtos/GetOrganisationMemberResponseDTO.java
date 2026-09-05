package com.mo.core.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.enums.MemberType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetOrganisationMemberResponseDTO {
    @JsonProperty("organisation_id")
    private String organisationId;

    @JsonProperty("organisation_name")
    private String organisationName;

    @JsonProperty("user_id")
    private String userId;
    
    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("member_type")
    private MemberType roles;

    
}
