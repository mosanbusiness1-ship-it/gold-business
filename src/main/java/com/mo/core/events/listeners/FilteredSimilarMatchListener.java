package com.mo.core.events.listeners;

import org.springframework.context.ApplicationListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.mo.core.dtos.ProductAndMatchedNeedsDTO;
import com.mo.core.events.FilteredSimilarMatchEvent;
import com.mo.core.services.audit.AuditLogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Listener pour les événements de correspondances similaires filtrées (Filtered Similar Matches)  
 * Persiste les événements pour audit trail et publie vers Kafka pour analyse BI
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FilteredSimilarMatchListener implements ApplicationListener<FilteredSimilarMatchEvent> {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AuditLogService auditLogService;
    private static final String TOPIC = "product-similar-matches";

    @Override
    public void onApplicationEvent(FilteredSimilarMatchEvent event) {
        ProductAndMatchedNeedsDTO data = event.getData();
        Long productId = data.getProduct().getId();
        
        try {
            log.info("[SIMILAR_MATCH_LISTENER] Traitement match similaire pour productId={}, matches count={}",
                    productId, data.getNeeds().size());

            // 1. Enregistrer dans audit log
            var auditLog = auditLogService.logEventSuccess(
                    "FILTERED_SIMILAR_MATCH",
                    String.format("Filtered similar match found for product %d with %d needs", productId, data.getNeeds().size()),
                    data,
                    null
            );

            // 2. Publier vers Kafka pour analytics BI
            try {
                kafkaTemplate.send(TOPIC, String.valueOf(productId), data);
                auditLogService.markAsPublished(auditLog.getId(), TOPIC);
                log.debug("[SIMILAR_MATCH_LISTENER] ✅ Événement publié vers Kafka topic '{}', auditId={}", TOPIC, auditLog.getId());
            } catch (Exception kafkaError) {
                auditLogService.markAsKafkaFailed(auditLog.getId(), TOPIC, kafkaError.getMessage());
                log.error("[SIMILAR_MATCH_LISTENER] ❌ Kafka publish failed, will retry later", kafkaError);
            }
        } catch (Exception e) {
            log.error("[SIMILAR_MATCH_LISTENER] ❌ Erreur lors du traitement du match similaire", e);
        }
    }
}
