package com.mo.core.dtos.organisationsDtos;

import com.mo.auth.User;
import com.mo.core.model.organisations.Organisation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatedOrganisationResponseDTO {
    User admin;
    Organisation organisation;
}
