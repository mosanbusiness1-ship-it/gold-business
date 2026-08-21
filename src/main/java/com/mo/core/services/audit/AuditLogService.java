package com.mo.core.services.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mo.core.model.audit.AuditLog;
import com.mo.core.model.audit.AuditStatus;
import com.mo.core.model.audit.KafkaPublishStatus;
import com.mo.core.repositories.audit.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service pour gérer les enregistrements d'audit
 * Responsable de persister les événements pour observabilité et recovery
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    /**
     * Enregistrer un événement d'audit avec succès
     */
    @Transactional
    public AuditLog logEventSuccess(String eventType, String description, Object eventData, Long userId) {
        try {
            String eventDataJson = serializeEventData(eventData);
            String correlationId = UUID.randomUUID().toString();

            AuditLog auditLog = new AuditLog();
            auditLog.setEventType(eventType);
            auditLog.setEventDescription(description);
            auditLog.setEventData(eventDataJson);
            auditLog.setUserId(userId);
            auditLog.setCorrelationId(correlationId);
            auditLog.setStatus(AuditStatus.SUCCESS);
            auditLog.setKafkaStatus(KafkaPublishStatus.PENDING);

            AuditLog saved = auditLogRepository.save(auditLog);
            log.debug("[AUDIT] ✅ Événement enregistré: type={}, correlationId={}", eventType, correlationId);
            return saved;
        } catch (Exception e) {
            log.error("[AUDIT] ❌ Erreur lors de l'enregistrement d'audit", e);
            throw new RuntimeException("Failed to log audit event", e);
        }
    }

    /**
     * Enregistrer un événement d'audit avec erreur
     */
    @Transactional
    public AuditLog logEventFailure(String eventType, String description, Object eventData, 
                                    Long userId, Exception error) {
        try {
            String eventDataJson = serializeEventData(eventData);
            String correlationId = UUID.randomUUID().toString();
            String stackTrace = getStackTrace(error);

            AuditLog auditLog = new AuditLog();
            auditLog.setEventType(eventType);
            auditLog.setEventDescription(description);
            auditLog.setEventData(eventDataJson);
            auditLog.setUserId(userId);
            auditLog.setCorrelationId(correlationId);
            auditLog.setStatus(AuditStatus.FAILED);
            auditLog.setErrorMessage(error.getMessage());
            auditLog.setErrorStacktrace(stackTrace);
            auditLog.setKafkaStatus(KafkaPublishStatus.PENDING);

            AuditLog saved = auditLogRepository.save(auditLog);
            log.warn("[AUDIT] ⚠️ Événement échoué enregistré: type={}, correlationId={}, error={}", 
                    eventType, correlationId, error.getMessage());
            return saved;
        } catch (Exception e) {
            log.error("[AUDIT] ❌ Erreur lors de l'enregistrement d'audit de failure", e);
            throw new RuntimeException("Failed to log audit event failure", e);
        }
    }

    /**
     * Marquer un audit log comme publié sur Kafka
     */
    @Transactional
    public void markAsPublished(Long auditLogId, String kafkaTopic) {
        try {
            AuditLog auditLog = auditLogRepository.findById(auditLogId)
                    .orElseThrow(() -> new RuntimeException("AuditLog not found: " + auditLogId));

            auditLog.setKafkaStatus(KafkaPublishStatus.PUBLISHED);
            auditLog.setKafkaTopic(kafkaTopic);
            auditLog.setPublishedAt(LocalDateTime.now());

            auditLogRepository.save(auditLog);
            log.debug("[AUDIT] ✅ Publication Kafka confirmée: auditLogId={}, topic={}", auditLogId, kafkaTopic);
        } catch (Exception e) {
            log.error("[AUDIT] ❌ Erreur lors du marquage comme publié", e);
        }
    }

    /**
     * Marquer un audit log comme échoué Kafka (potentiel dead-letter)
     */
    @Transactional
    public void markAsKafkaFailed(Long auditLogId, String topic, String errorMessage) {
        try {
            AuditLog auditLog = auditLogRepository.findById(auditLogId)
                    .orElseThrow(() -> new RuntimeException("AuditLog not found: " + auditLogId));

            auditLog.setKafkaStatus(KafkaPublishStatus.RETRY_PENDING);
            auditLog.setKafkaTopic(topic);
            auditLog.setErrorMessage(errorMessage);

            auditLogRepository.save(auditLog);
            log.warn("[AUDIT] ⚠️ Kafka publish failed (retry pending): auditLogId={}, topic={}", auditLogId, topic);
        } catch (Exception e) {
            log.error("[AUDIT] ❌ Erreur lors du marquage comme échoué Kafka", e);
        }
    }

    /**
     * Obtenir tous les logs en attente de publication Kafka
     */
    public List<AuditLog> getPendingKafkaPublish() {
        return auditLogRepository.findPendingKafkaPublish();
    }

    /**
     * Obtenir les logs échoués
     */
    public List<AuditLog> getFailedLogs() {
        return auditLogRepository.findFailedLogs();
    }

    /**
     * Récupérer une trace de corrélation complète
     */
    public List<AuditLog> getCorrelationChain(String correlationId) {
        return auditLogRepository.findByCorrelationId(correlationId);
    }

    // === Helper Methods ===

    private String serializeEventData(Object eventData) {
        try {
            return objectMapper.writeValueAsString(eventData);
        } catch (Exception e) {
            log.warn("[AUDIT] Impossible de sérialiser les données d'événement: {}", e.getMessage());
            return eventData != null ? eventData.toString() : "null";
        }
    }

    private String getStackTrace(Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.getClass().getName()).append(": ").append(e.getMessage()).append("\n");
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append("\tat ").append(element).append("\n");
        }
        return sb.toString();
    }
}
