package com.mo.core.model.audit;

/**
 * Énumération des statuts de publication Kafka
 */
public enum KafkaPublishStatus {
    /**
     * Pas encore publié
     */
    PENDING,

    /**
     * Publié avec succès
     */
    PUBLISHED,

    /**
     * Échec publication
     */
    FAILED,

    /**
     * En attente de retry
     */
    RETRY_PENDING,

    /**
     * Déplacé à la dead-letter queue
     */
    DEAD_LETTERED
}
