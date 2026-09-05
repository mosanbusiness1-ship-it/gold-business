package com.mo.core.dtos;

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
    private Long organisationId;
    private String organisationName;
    private OrganisationType organisationType;
    private MemberType memberType;
}
