package com.mo.configuration.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Configuration des topics Kafka pour les événements d'audit et dead-letter queues
 * Crée les topics nécessaires ainsi que les variantes DLQ (Dead-Letter Queue) pour chaque topic
 */
@Configuration
public class KafkaAuditTopicsConfiguration {

    // Topics principaux
    static final String TOPIC_PRODUCT_STRICT_MATCHES = "product-strict-matches";
    static final String TOPIC_PRODUCT_SIMILAR_MATCHES = "product-similar-matches";
    static final String TOPIC_PRODUCT_DELETIONS = "product-deletions-audit";
    static final String TOPIC_PRODUCT_INDEXING = "product-indexing-events";
    static final String TOPIC_AUCTION_NOTIFICATIONS = "notify-user-for-winningAuction";

    // Dead-letter queues (suffix: -dlq)
    static final String TOPIC_PRODUCT_STRICT_MATCHES_DLQ = "product-strict-matches-dlq";
    static final String TOPIC_PRODUCT_SIMILAR_MATCHES_DLQ = "product-similar-matches-dlq";
    static final String TOPIC_PRODUCT_DELETIONS_DLQ = "product-deletions-audit-dlq";
    static final String TOPIC_PRODUCT_INDEXING_DLQ = "product-indexing-events-dlq";
    static final String TOPIC_AUCTION_NOTIFICATIONS_DLQ = "notify-user-for-winningAuction-dlq";

    // === Topics Principaux ===

    @Bean
    public NewTopic productStrictMatchesTopic() {
        return TopicBuilder.name(TOPIC_PRODUCT_STRICT_MATCHES)
                .partitions(3)
                .replicas(1)
                .config("retention.ms", String.valueOf(7 * 24 * 60 * 60 * 1000)) // 7 jours
                .build();
    }

    @Bean
    public NewTopic productSimilarMatchesTopic() {
        return TopicBuilder.name(TOPIC_PRODUCT_SIMILAR_MATCHES)
                .partitions(3)
                .replicas(1)
                .config("retention.ms", String.valueOf(7 * 24 * 60 * 60 * 1000))
                .build();
    }

    @Bean
    public NewTopic productDeletionsTopic() {
        return TopicBuilder.name(TOPIC_PRODUCT_DELETIONS)
                .partitions(2)
                .replicas(1)
                .config("retention.ms", String.valueOf(30 * 24 * 60 * 60 * 1000)) // 30 jours (audit critique)
                .build();
    }

    @Bean
    public NewTopic productIndexingTopic() {
        return TopicBuilder.name(TOPIC_PRODUCT_INDEXING)
                .partitions(2)
                .replicas(1)
                .config("retention.ms", String.valueOf(7 * 24 * 60 * 60 * 1000))
                .build();
    }

    @Bean
    public NewTopic auctionNotificationsTopic() {
        return TopicBuilder.name(TOPIC_AUCTION_NOTIFICATIONS)
                .partitions(2)
                .replicas(1)
                .config("retention.ms", String.valueOf(30 * 24 * 60 * 60 * 1000)) // 30 jours (critique)
                .build();
    }

    // === Dead-Letter Queue Topics ===

    @Bean
    public NewTopic productStrictMatchesDlqTopic() {
        return TopicBuilder.name(TOPIC_PRODUCT_STRICT_MATCHES_DLQ)
                .partitions(1)
                .replicas(1)
                .config("retention.ms", String.valueOf(30 * 24 * 60 * 60 * 1000)) // 30 jours
                .build();
    }

    @Bean
    public NewTopic productSimilarMatchesDlqTopic() {
        return TopicBuilder.name(TOPIC_PRODUCT_SIMILAR_MATCHES_DLQ)
                .partitions(1)
                .replicas(1)
                .config("retention.ms", String.valueOf(30 * 24 * 60 * 60 * 1000))
                .build();
    }

    @Bean
    public NewTopic productDeletionsDlqTopic() {
        return TopicBuilder.name(TOPIC_PRODUCT_DELETIONS_DLQ)
                .partitions(1)
                .replicas(1)
                .config("retention.ms", String.valueOf(90 * 24 * 60 * 60 * 1000)) // 90 jours (très critique)
                .build();
    }

    @Bean
    public NewTopic productIndexingDlqTopic() {
        return TopicBuilder.name(TOPIC_PRODUCT_INDEXING_DLQ)
                .partitions(1)
                .replicas(1)
                .config("retention.ms", String.valueOf(30 * 24 * 60 * 60 * 1000))
                .build();
    }

    @Bean
    public NewTopic auctionNotificationsDlqTopic() {
        return TopicBuilder.name(TOPIC_AUCTION_NOTIFICATIONS_DLQ)
                .partitions(1)
                .replicas(1)
                .config("retention.ms", String.valueOf(90 * 24 * 60 * 60 * 1000)) // 90 jours (critique)
                .build();
    }
}
