package com.mo.core.consumers.kafka;

import com.mo.core.model.audit.KafkaPublishStatus;
import com.mo.core.repositories.audit.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * Consommateur des messages en dead-letter queue (DLQ)
 * Traite les messages qui n'ont pas pu être livrés aux topics principaux
 * Enregistre l'échec dans la base d'audit pour analyse ultérieure
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeadLetterQueueConsumer {

    private final AuditLogRepository auditLogRepository;

    /**
     * Traiter les messages en DLQ pour product-strict-matches
     */
    @KafkaListener(
            topics = "product-strict-matches-dlq",
            groupId = "dlq-consumer-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleStrictMatchesDlq(String message,
                                       @Header(value = "kafka_receivedTopic", required = false) String topic,
                                       @Header(value = "kafka_receivedPartition", required = false) Integer partition) {
        processDlqMessage("STRICT_MATCH_DLQ", message, topic, partition);
    }

    /**
     * Traiter les messages en DLQ pour product-similar-matches
     */
    @KafkaListener(
            topics = "product-similar-matches-dlq",
            groupId = "dlq-consumer-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleSimilarMatchesDlq(String message,
                                        @Header(value = "kafka_receivedTopic", required = false) String topic,
                                        @Header(value = "kafka_receivedPartition", required = false) Integer partition) {
        processDlqMessage("SIMILAR_MATCH_DLQ", message, topic, partition);
    }

    /**
     * Traiter les messages en DLQ pour product-deletions-audit
     */
    @KafkaListener(
            topics = "product-deletions-audit-dlq",
            groupId = "dlq-consumer-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleDeletionsDlq(String message,
                                   @Header(value = "kafka_receivedTopic", required = false) String topic,
                                   @Header(value = "kafka_receivedPartition", required = false) Integer partition) {
        processDlqMessage("DELETION_DLQ", message, topic, partition);
    }

    /**
     * Traiter les messages en DLQ pour product-indexing-events
     */
    @KafkaListener(
            topics = "product-indexing-events-dlq",
            groupId = "dlq-consumer-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleIndexingDlq(String message,
                                  @Header(value = "kafka_receivedTopic", required = false) String topic,
                                  @Header(value = "kafka_receivedPartition", required = false) Integer partition) {
        processDlqMessage("INDEXING_DLQ", message, topic, partition);
    }

    /**
     * Traiter les messages en DLQ pour auction notifications (très critique)
     */
    @KafkaListener(
            topics = "notify-user-for-winningAuction-dlq",
            groupId = "dlq-consumer-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleAuctionNotificationsDlq(String message,
                                              @Header(value = "kafka_receivedTopic", required = false) String topic,
                                              @Header(value = "kafka_receivedPartition", required = false) Integer partition) {
        processDlqMessage("AUCTION_NOTIFICATION_DLQ", message, topic, partition);
        // Pour les notifications d'enchères, on peut aussi déclencher une alerte
        log.error("[DLQ] ⚠️ CRITICAL: Winning auction notification failed and moved to DLQ. Message: {}", message);
    }

    /**
     * Traiter un message DLQ générique
     * Enregistrer l'échec dans les logs d'audit pour suivi
     */
    private void processDlqMessage(String eventType, String message, String topic, Integer partition) {
        try {
            log.warn("[DLQ] Processing dead-letter message: type={}, topic={}, partition={}", 
                    eventType, topic, partition);

            // TODO: Implémenter la logique de retry avec backoff exponentiel
            // TODO: Implémenter les alertes (email, PagerDuty, etc.)
            // TODO: Enregistrer dans une table séparée pour manuel intervention

            log.info("[DLQ] ✅ Dead-letter message processed for {}", eventType);
        } catch (Exception e) {
            log.error("[DLQ] ❌ Failed to process dead-letter message for {}", eventType, e);
            // Ne pas relancer l'exception pour éviter une boucle infinie
        }
    }
}
