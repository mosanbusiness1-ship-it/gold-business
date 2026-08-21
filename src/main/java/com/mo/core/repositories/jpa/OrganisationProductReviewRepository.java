package com.mo.core.repositories.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mo.core.model.organisations.OrganisationProductReview;
import com.mo.core.model.organisations.OrganisationProductReviewId;

@Repository
public interface OrganisationProductReviewRepository extends JpaRepository<OrganisationProductReview, OrganisationProductReviewId> {
    Optional<OrganisationProductReview> findByIdOrganisationIdAndIdProductId(Long organisationId, Long productId);
    @org.springframework.data.jpa.repository.Query("SELECT r FROM OrganisationProductReview r WHERE r.organisation.id = :orgId")
    java.util.List<OrganisationProductReview> findByOrganisationId(@org.springframework.data.repository.query.Param("orgId") Long orgId);
}
