package com.mo.core.events.listeners;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.mo.core.events.ProductIndexingEvent;
import com.mo.core.services.audit.AuditLogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Listener pour les événements d'indexation de produit
 * Persiste pour audit trail et publie vers Kafka pour pipeline Elasticsearch
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ProductIndexingListener implements ApplicationListener<ProductIndexingEvent> {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AuditLogService auditLogService;
    private static final String TOPIC = "product-indexing-events";

    @Override
    public void onApplicationEvent(ProductIndexingEvent event) {
        String data = event.getData();
        
        try {
            log.info("[PRODUCT_INDEXING_LISTENER] Indexation de produit: {}", 
                    data.substring(0, Math.min(50, data.length())));

            // 1. Enregistrer dans audit log
            var auditLog = auditLogService.logEventSuccess(
                    "PRODUCT_INDEXING",
                    "Product indexing event",
                    event,
                    null
            );

            // 2. Publier vers Kafka pour pipeline ES
            try {
                kafkaTemplate.send(TOPIC, event.getData(), event);
                auditLogService.markAsPublished(auditLog.getId(), TOPIC);
                log.debug("[PRODUCT_INDEXING_LISTENER] ✅ Événement d'indexation publié vers Kafka topic '{}', auditId={}", TOPIC, auditLog.getId());
            } catch (Exception kafkaError) {
                auditLogService.markAsKafkaFailed(auditLog.getId(), TOPIC, kafkaError.getMessage());
                log.error("[PRODUCT_INDEXING_LISTENER] ❌ Kafka publish failed, will retry later", kafkaError);
            }
        } catch (Exception e) {
            log.error("[PRODUCT_INDEXING_LISTENER] ❌ Erreur lors du traitement de l'indexation", e);
        }
    }
}
