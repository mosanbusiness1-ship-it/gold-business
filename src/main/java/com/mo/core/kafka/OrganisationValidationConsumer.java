package com.mo.core.kafka;

import com.mo.core.events.OrganisationProductValidationEvent;
import com.mo.core.services.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer for organisation product validation events.
 * Handles PENDING, APPROVED, and REJECTED validation workflows.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class OrganisationValidationConsumer {
    private final WebhookService webhookService;

    /**
     * Consumer for validation pending events.
     * When a product is submitted, it's queued for moderation.
     */
    @KafkaListener(
        topics = "${kafka.topics.org-validation-pending:org-validation-pending}",
        groupId = "${kafka.consumer.group-id:org-moderation-pending}",
        containerFactory = "organisationValidationListenerContainerFactory"
    )
    public void handleValidationPending(OrganisationProductValidationEvent event) {
        log.info("📥 Received PENDING validation event: orgId={}, productId={}", 
            event.getOrganisationId(), event.getProductId());
        
        try {
            // Log event for audit trail
            log.info("🔔 Product {} from organisation {} submitted for validation at {}", 
                event.getProductId(), event.getOrganisationId(), event.getCreatedAt());
            
            // Fire webhooks for PENDING
            webhookService.dispatch(event);
            
            // Future: Insert into moderation queue, notify moderators, etc.
            
        } catch (Exception e) {
            log.error("❌ Error processing PENDING event", e);
        }
    }

    /**
     * Consumer for validation approved events.
     */
    @KafkaListener(
        topics = "${kafka.topics.org-validation-approved:org-validation-approved}",
        groupId = "${kafka.consumer.group-id:org-moderation-approved}",
        containerFactory = "organisationValidationListenerContainerFactory"
    )
    public void handleValidationApproved(OrganisationProductValidationEvent event) {
        log.info("📥 Received APPROVED validation event: orgId={}, productId={}, moderatorId={}", 
            event.getOrganisationId(), event.getProductId(), event.getModeratorId());
        
        try {
            log.info("✅ Product {} approved by moderator {}. SLA exceeded: {}", 
                event.getProductId(), event.getModeratorId(), event.isSlaExceeded());
            
            if (event.isSlaExceeded()) {
                log.warn("⚠️ SLA Exceeded: Validation took {} minutes (> 1440)", 
                    event.getSlaMinutesElapsed());
            }
            
            // Fire webhooks for approval
            webhookService.dispatch(event);
            
            // Future: Send notification to org, update product status, trigger next workflow step
            
        } catch (Exception e) {
            log.error("❌ Error processing APPROVED event", e);
        }
    }

    /**
     * Consumer for validation rejected events.
     */
    @KafkaListener(
        topics = "${kafka.topics.org-validation-rejected:org-validation-rejected}",
        groupId = "${kafka.consumer.group-id:org-moderation-rejected}",
        containerFactory = "organisationValidationListenerContainerFactory"
    )
    public void handleValidationRejected(OrganisationProductValidationEvent event) {
        log.info("📥 Received REJECTED validation event: orgId={}, productId={}, reason={}", 
            event.getOrganisationId(), event.getProductId(), event.getComments());
        
        try {
            log.info("❌ Product {} rejected by moderator {} | Reason: {}", 
                event.getProductId(), event.getModeratorId(), event.getComments());
            
            if (event.isSlaExceeded()) {
                log.warn("⚠️ SLA Exceeded during rejection: {} minutes", event.getSlaMinutesElapsed());
            }
            
            // Fire webhooks for rejection
            webhookService.dispatch(event);
            
            // Future: Notify org of rejection, allow resubmission, update stats
            
        } catch (Exception e) {
            log.error("❌ Error processing REJECTED event", e);
        }
    }
}
