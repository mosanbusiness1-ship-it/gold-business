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

    @JsonProperty("member_id")
    private String memberId;
    
    @JsonProperty("member_full_name")
    private String memberFullName;

    @JsonProperty("member_email")
    private String memberEmail;

    @JsonProperty("member_type")
    private MemberType memberType;

    
}
