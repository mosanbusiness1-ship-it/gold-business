package com.mo.core.kafka;

import com.mo.core.events.OrganisationProductValidationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Kafka producer for organisation product validation events.
 */
@Slf4j
@Service
public class OrganisationValidationProducer {

    private final KafkaTemplate<String, OrganisationProductValidationEvent> kafkaTemplate;

    @Value("${kafka.topics.org-validation-pending:org-validation-pending}")
    private String topicPending;

    @Value("${kafka.topics.org-validation-approved:org-validation-approved}")
    private String topicApproved;

    @Value("${kafka.topics.org-validation-rejected:org-validation-rejected}")
    private String topicRejected;

    public OrganisationValidationProducer() {
        this.kafkaTemplate = null;
    }

    @Autowired(required = false)
    public OrganisationValidationProducer(KafkaTemplate<String, OrganisationProductValidationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Emit a validation pending event when a product is submitted for validation.
     */
    public void emitValidationPending(OrganisationProductValidationEvent event) {
        if (kafkaTemplate == null) {
            log.warn("Kafka désactivé ou non configuré : impossible d’émettre l’événement PENDING pour orgId={}, productId={}",
                event.getOrganisationId(), event.getProductId());
            return;
        }
        event.setEventType("PENDING");
        log.info("🔔 Emitting PENDING validation event: orgId={}, productId={}", 
            event.getOrganisationId(), event.getProductId());
        kafkaTemplate.send(topicPending, generateKey(event), event)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("✅ PENDING event sent successfully");
                } else {
                    log.error("❌ Failed to send PENDING event", ex);
                }
            });
    }

    /**
     * Emit a validation approved event.
     */
    public void emitValidationApproved(OrganisationProductValidationEvent event) {
        if (kafkaTemplate == null) {
            log.warn("Kafka désactivé ou non configuré : impossible d’émettre l’événement APPROVED pour orgId={}, productId={}",
                event.getOrganisationId(), event.getProductId());
            return;
        }
        event.setEventType("APPROVED");
        log.info("✅ Emitting APPROVED validation event: orgId={}, productId={}", 
            event.getOrganisationId(), event.getProductId());
        kafkaTemplate.send(topicApproved, generateKey(event), event)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("✅ APPROVED event sent successfully");
                } else {
                    log.error("❌ Failed to send APPROVED event", ex);
                }
            });
    }

    /**
     * Emit a validation rejected event.
     */
    public void emitValidationRejected(OrganisationProductValidationEvent event) {
        if (kafkaTemplate == null) {
            log.warn("Kafka désactivé ou non configuré : impossible d’émettre l’événement REJECTED pour orgId={}, productId={}",
                event.getOrganisationId(), event.getProductId());
            return;
        }
        event.setEventType("REJECTED");
        log.info("❌ Emitting REJECTED validation event: orgId={}, productId={}, reason={}", 
            event.getOrganisationId(), event.getProductId(), event.getComments());
        kafkaTemplate.send(topicRejected, generateKey(event), event)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("✅ REJECTED event sent successfully");
                } else {
                    log.error("❌ Failed to send REJECTED event", ex);
                }
            });
    }

    /**
     * Generate a Kafka message key based on orgId and productId.
     */
    private String generateKey(OrganisationProductValidationEvent event) {
        return event.getOrganisationId() + "-" + event.getProductId();
    }
}
