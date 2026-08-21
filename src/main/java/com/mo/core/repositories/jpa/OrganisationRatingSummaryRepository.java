package com.mo.core.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mo.core.model.organisations.OrganisationRatingSummary;

@Repository
public interface OrganisationRatingSummaryRepository extends JpaRepository<OrganisationRatingSummary, Long> {
}
