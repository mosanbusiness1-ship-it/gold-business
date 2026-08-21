package com.mo.core.repositories.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mo.core.model.organisations.CommissionTransaction;

@Repository
public interface CommissionTransactionRepository extends JpaRepository<CommissionTransaction, Long> {
    List<CommissionTransaction> findByOrganisationId(Long organisationId);

    @Query("SELECT c FROM CommissionTransaction c WHERE c.organisation.id = :orgId AND c.status = 'PENDING'")
    List<CommissionTransaction> findPendingByOrganisationId(@Param("orgId") Long organisationId);

    // find by transaction reference (used to link commission tx to escrow)
    CommissionTransaction findByTransactionRef(String transactionRef);
}
