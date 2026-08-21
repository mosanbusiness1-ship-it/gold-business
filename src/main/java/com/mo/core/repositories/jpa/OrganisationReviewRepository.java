package com.mo.core.repositories.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mo.core.model.organisations.OrganisationReview;

@Repository
public interface OrganisationReviewRepository extends JpaRepository<OrganisationReview, Long> {
    Page<OrganisationReview> findByOrganisationId(Long organisationId, Pageable pageable);

    @Query("SELECT r FROM OrganisationReview r WHERE r.organisation.id = :orgId AND r.isVerifiedPurchase = true")
    Page<OrganisationReview> findVerifiedByOrganisationId(@Param("orgId") Long organisationId, Pageable pageable);

    List<OrganisationReview> findByOrganisationId(Long organisationId);
}
