package com.mo.core.repositories.audit;

import com.mo.core.model.audit.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour accéder aux enregistrements d'audit
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Trouver tous les logs d'audit pour un type d'événement spécifique
     */
    List<AuditLog> findByEventType(String eventType);

    /**
     * Trouver tous les logs échoués nécessitant un retry
     */
    @Query("SELECT a FROM AuditLog a WHERE a.status = 'FAILED' OR a.kafkaStatus = 'FAILED'")
    List<AuditLog> findFailedLogs();

    /**
     * Trouver les logs en attente de publication Kafka
     */
    @Query("SELECT a FROM AuditLog a WHERE a.kafkaStatus = 'PENDING' AND a.status = 'SUCCESS'")
    List<AuditLog> findPendingKafkaPublish();

    /**
     * Trouver los par ID de corrélation (pour tracer les chaînes d'événements)
     */
    List<AuditLog> findByCorrelationId(String correlationId);

    /**
     * Trouver les logs créés dans une plage de temps
     */
    List<AuditLog> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Trouver les logs par utilisateur
     */
    List<AuditLog> findByUserId(Long userId);

    /**
     * Compter les logs par type d'événement (pour analytics)
     */
    @Query("SELECT a.eventType, COUNT(a) FROM AuditLog a GROUP BY a.eventType")
    List<Object[]> countByEventType();
}
