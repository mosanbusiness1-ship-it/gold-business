package com.mo.core.repositories.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mo.core.model.needs.NeedOffer;

public interface NeedOfferRepository extends JpaRepository<NeedOffer, Long> {
    List<NeedOffer> findByNeedId(Long needId);
    List<NeedOffer> findByOrganisationId(Long organisationId);
}
