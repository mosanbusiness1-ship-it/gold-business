package com.mo.core.events.listeners;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.mo.core.events.ProductDeletionEvent;
import com.mo.core.services.audit.AuditLogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Listener pour les événements de suppression de produit
 * Persiste pour audit trail et publie vers Kafka pour nettoyage ES et traçabilité
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ProductDeletionListener implements ApplicationListener<ProductDeletionEvent> {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AuditLogService auditLogService;
    private static final String TOPIC = "product-deletions-audit";

    @Override
    public void onApplicationEvent(ProductDeletionEvent event) {
        Long productId = event.getProductId();
        
        try {
            log.info("[PRODUCT_DELETION_LISTENER] Suppression produit productId={}", productId);

            // 1. Enregistrer dans audit log
            var auditLog = auditLogService.logEventSuccess(
                    "PRODUCT_DELETION",
                    String.format("Product deleted: %d", productId),
                    event,
                    null
            );

            // 2. Publier vers Kafka pour audit trail
            try {
                kafkaTemplate.send(TOPIC, String.valueOf(productId), event);
                auditLogService.markAsPublished(auditLog.getId(), TOPIC);
                log.debug("[PRODUCT_DELETION_LISTENER] ✅ Événement de suppression publié vers Kafka topic '{}', auditId={}", TOPIC, auditLog.getId());
            } catch (Exception kafkaError) {
                auditLogService.markAsKafkaFailed(auditLog.getId(), TOPIC, kafkaError.getMessage());
                log.error("[PRODUCT_DELETION_LISTENER] ❌ Kafka publish failed, will retry later", kafkaError);
            }
        } catch (Exception e) {
            log.error("[PRODUCT_DELETION_LISTENER] ❌ Erreur lors du traitement de la suppression", e);
        }
    }
}
