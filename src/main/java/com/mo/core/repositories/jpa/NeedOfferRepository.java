package com.mo.core.repositories.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mo.core.model.needs.NeedOffer;

public interface NeedOfferRepository extends JpaRepository<NeedOffer, Long> {
    @EntityGraph(attributePaths = {"need", "product", "offerer", "organisation"})
    List<NeedOffer> findByNeedId(Long needId);
    List<NeedOffer> findByOrganisationId(Long organisationId);
}
