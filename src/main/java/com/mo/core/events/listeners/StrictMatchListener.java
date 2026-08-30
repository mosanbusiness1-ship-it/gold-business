package com.mo.core.events.listeners;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.mo.core.dtos.ProductAndMatchedNeedsDTO;
import com.mo.core.events.StrictMatchEvent;
import com.mo.core.services.audit.AuditLogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Listener pour les événements de correspondances strictes (Strict Matches)
 * Persiste les événements pour audit trail et publie vers Kafka pour analyse BI
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class StrictMatchListener implements ApplicationListener<StrictMatchEvent> {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AuditLogService auditLogService;
    private static final String TOPIC = "product-strict-matches";

    @Override
    public void onApplicationEvent(StrictMatchEvent event) {
        ProductAndMatchedNeedsDTO data = event.getData();
        Long productId = data.getProduct().getId();
        
        try {
            log.info("[STRICT_MATCH_LISTENER] Traitement match strict pour productId={}, matches count={}",
                    productId, data.getNeeds().size());

            // 1. Enregistrer dans audit log
            var auditLog = auditLogService.logEventSuccess(
                    "STRICT_MATCH",
                    String.format("Strict match found for product %d with %d needs", productId, data.getNeeds().size()),
                    data,
                    null // userId not available in this context
            );

            // 2. Publier vers Kafka pour analytics BI
            try {
                kafkaTemplate.send(TOPIC, String.valueOf(productId), data);
                auditLogService.markAsPublished(auditLog.getId(), TOPIC);
                log.debug("[STRICT_MATCH_LISTENER] ✅ Événement publié vers Kafka topic '{}', auditId={}", TOPIC, auditLog.getId());
            } catch (Exception kafkaError) {
                auditLogService.markAsKafkaFailed(auditLog.getId(), TOPIC, kafkaError.getMessage());
                log.error("[STRICT_MATCH_LISTENER] ❌ Kafka publish failed, will retry later", kafkaError);
            }
        } catch (Exception e) {
            log.error("[STRICT_MATCH_LISTENER] ❌ Erreur lors du traitement du match strict", e);
            // Audit failure already logged by auditLogService
        }
    }
}
