package com.mo.core.documents.organisations;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganisationMemberId implements Serializable {
    private Long organisationId;
    private Long userId;
}

