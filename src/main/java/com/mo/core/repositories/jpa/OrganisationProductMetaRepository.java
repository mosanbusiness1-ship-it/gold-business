package com.mo.core.repositories.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mo.core.model.organisations.OrganisationProductMeta;
import com.mo.core.model.organisations.OrganisationProductMetaId;

@Repository
public interface OrganisationProductMetaRepository extends JpaRepository<OrganisationProductMeta, OrganisationProductMetaId> {
    Optional<OrganisationProductMeta> findByIdOrganisationIdAndIdProductId(Long organisationId, Long productId);
}
