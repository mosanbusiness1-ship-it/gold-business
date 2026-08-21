package com.mo.core.repositories.jpa;

import com.mo.core.model.organisations.EscrowTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EscrowTransactionRepository extends JpaRepository<EscrowTransaction, Long> {
    List<EscrowTransaction> findByOrganisationId(Long organisationId);
}
