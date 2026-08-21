package com.mo.core.model.audit;

/**
 * Énumération des statuts d'enregistrement d'audit
 */
public enum AuditStatus {
    /**
     * Événement enregistré mais pas encore traité
     */
    PENDING,

    /**
     * Événement traité avec succès
     */
    SUCCESS,

    /**
     * Événement échoué
     */
    FAILED,

    /**
     * Événement en attente de retry
     */
    RETRY_PENDING
}
