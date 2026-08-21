package com.mo.core.repositories.jpa;

import com.mo.core.model.organisations.WebhookSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebhookSubscriptionRepository extends JpaRepository<WebhookSubscription, Long> {
    List<WebhookSubscription> findByOrganisationIdAndActiveTrue(Long organisationId);
}
