package com.mo.core.repositories.jpa;

import com.mo.core.model.organisations.GuaranteeClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuaranteeClaimRepository extends JpaRepository<GuaranteeClaim, Long> {
    List<GuaranteeClaim> findByOrganisationId(Long organisationId);
}
