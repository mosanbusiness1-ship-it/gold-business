package com.mo.core.repositories.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mo.core.model.organisations.GuaranteePolicy;

@Repository
public interface GuaranteePolicyRepository extends JpaRepository<GuaranteePolicy, Long> {
    List<GuaranteePolicy> findByOrganisationId(Long organisationId);
}
