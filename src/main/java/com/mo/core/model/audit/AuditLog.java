package com.mo.core.model.audit;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entité pour enregistrer tous les événements de l'application
 * Utilisée pour audit trail, observabilité et recovery
 */
@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_event_type", columnList = "event_type"),
    @Index(name = "idx_audit_created_at", columnList = "created_at"),
    @Index(name = "idx_audit_status", columnList = "status"),
    @Index(name = "idx_audit_correlation_id", columnList = "correlation_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /**
     * Type d'événement (ex: STRICT_MATCH, SIMILAR_MATCH, PRODUCT_DELETION, etc.)
     */
    @Column(name = "event_type", nullable = false, length = 100)
    @ToString.Include
    private String eventType;

    /**
     * Description courte de l'événement
     */
    @Column(name = "event_description", length = 255)
    private String eventDescription;

    /**
     * Données d'événement sérialisées en JSON
     */
    @Column(name = "event_data", columnDefinition = "TEXT")
    private String eventData;

    /**
     * ID utilisateur qui a déclenché l'événement (si applicable)
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * Identifiant de corrélation pour tracer les chaînes d'événements
     */
    @Column(name = "correlation_id", length = 36)
    private String correlationId;

    /**
     * Statut de traitement: SUCCESS, FAILED, PENDING
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AuditStatus status = AuditStatus.PENDING;

    /**
     * Message d'erreur si applicable
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * Stack trace en cas d'erreur
     */
    @Column(name = "error_stacktrace", columnDefinition = "TEXT")
    private String errorStacktrace;

    /**
     * Statut de publication Kafka
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "kafka_status", nullable = false, length = 20)
    private KafkaPublishStatus kafkaStatus = KafkaPublishStatus.PENDING;

    /**
     * Topic Kafka destination
     */
    @Column(name = "kafka_topic", length = 100)
    private String kafkaTopic;

    /**
     * Date/heure de création automatique
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @ToString.Include
    private LocalDateTime createdAt;

    /**
     * Date/heure de dernière mise à jour
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Date/heure de publication Kafka
     */
    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
