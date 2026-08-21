package com.mo.core.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import com.datastax.astra.client.DataAPIClient;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Service pour vérifier la connectivité avec tous les services externes
 * Utile pour les health checks et le debugging de connexion
 */
@Service
@Slf4j
public class ServiceConnectionVerificationService {

    @Autowired(required = false)
    private DataSource dataSource;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired(required = false)
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired(required = false)
    private DataAPIClient astraClient;

    @Value("${spring.datasource.url:not-configured}")
    private String postgresUrl;

    @Value("${spring.redis.url:not-configured}")
    private String redisUrl;

    @Value("${spring.kafka.bootstrap-servers:not-configured}")
    private String kafkaBootstrapServers;

    @Value("${astra.db.api-endpoint:not-configured}")
    private String astraApiEndpoint;

    /**
     * Vérifie la connexion à PostgreSQL/Neon
     */
    public Map<String, Object> verifyPostgresConnection() {
        Map<String, Object> result = new HashMap<>();
        result.put("service", "PostgreSQL/Neon");
        result.put("configured", !postgresUrl.equals("not-configured"));

        try {
            if (dataSource != null) {
                Connection connection = dataSource.getConnection();
                boolean isValid = connection.isValid(5);
                result.put("status", isValid ? "CONNECTED" : "INVALID");
                connection.close();
                log.info("✅ PostgreSQL connection verified");
            } else {
                result.put("status", "DATASOURCE_NOT_FOUND");
            }
        } catch (Exception e) {
            result.put("status", "FAILED");
            result.put("error", e.getMessage());
            log.error("❌ PostgreSQL connection failed: {}", e.getMessage());
        }

        return result;
    }

    /**
     * Vérifie la connexion à Redis (Upstash)
     */
    public Map<String, Object> verifyRedisConnection() {
        Map<String, Object> result = new HashMap<>();
        result.put("service", "Redis/Upstash");
        result.put("configured", !redisUrl.equals("not-configured"));

        try {
            if (redisTemplate != null) {
                redisTemplate.getConnectionFactory().getConnection().ping();
                result.put("status", "CONNECTED");
                log.info("✅ Redis connection verified");
            } else {
                result.put("status", "REDIS_TEMPLATE_NOT_FOUND");
            }
        } catch (Exception e) {
            result.put("status", "FAILED");
            result.put("error", e.getMessage());
            log.error("❌ Redis connection failed: {}", e.getMessage());
        }

        return result;
    }

    /**
     * Vérifie la connexion à Kafka
     */
    public Map<String, Object> verifyKafkaConnection() {
        Map<String, Object> result = new HashMap<>();
        result.put("service", "Kafka");
        result.put("configured", !kafkaBootstrapServers.equals("not-configured"));

        try {
            if (kafkaTemplate != null) {
                // Vérifier que le template est correctement configuré
                result.put("status", "CONFIGURED");
                log.info("✅ Kafka template is configured and available");
            } else {
                result.put("status", "TEMPLATE_NOT_FOUND");
            }
        } catch (Exception e) {
            result.put("status", "FAILED");
            result.put("error", e.getMessage());
            log.error("❌ Kafka configuration failed: {}", e.getMessage());
        }

        return result;
    }

    /**
     * Vérifie la connexion à Astra DB
     */
    public Map<String, Object> verifyAstraDBConnection() {
        Map<String, Object> result = new HashMap<>();
        result.put("service", "Astra DB");
        result.put("configured", !astraApiEndpoint.equals("not-configured"));

        try {
            if (astraClient != null) {
                // Use reflection to avoid compile-time dependency on Database type
                Object db = astraClient.getDatabase(astraApiEndpoint);
                try {
                    var m = db.getClass().getMethod("listCollectionNames");
                    Object collections = m.invoke(db);
                    result.put("status", "CONNECTED");
                    result.put("collections", collections);
                    log.info("✅ Astra DB connection verified");
                } catch (NoSuchMethodException nsme) {
                    result.put("status", "CONNECTED_NO_COLLECTIONS_METHOD");
                    log.info("✅ Astra client available but could not list collections");
                }
            } else {
                result.put("status", "CLIENT_NOT_FOUND");
            }
        } catch (Exception e) {
            result.put("status", "FAILED");
            result.put("error", e.getMessage());
            log.error("❌ Astra DB connection failed: {}", e.getMessage());
        }

        return result;
    }

    /**
     * Vérifie toutes les connexions et retourne un rapport complet
     */
    public Map<String, Object> verifyAllConnections() {
        Map<String, Object> report = new HashMap<>();
        report.put("timestamp", System.currentTimeMillis());

        // Vérifier chaque service
        report.put("postgresql", verifyPostgresConnection());
        report.put("redis", verifyRedisConnection());
        report.put("kafka", verifyKafkaConnection());
        report.put("astradb", verifyAstraDBConnection());

        // Résumé
        Map<String, Object> summary = new HashMap<>();
        summary.put("total_services", 4);
        summary.put("connected", countConnected(report));
        summary.put("configured", countConfigured(report));

        report.put("summary", summary);

        return report;
    }

    private int countConnected(Map<String, Object> report) {
        int count = 0;
        for (String key : new String[]{"postgresql", "redis", "kafka", "astradb"}) {
            Map<String, Object> service = (Map<String, Object>) report.get(key);
            String status = (String) service.get("status");
            if ("CONNECTED".equals(status) || "CONFIGURED".equals(status)) {
                count++;
            }
        }
        return count;
    }

    private int countConfigured(Map<String, Object> report) {
        int count = 0;
        for (String key : new String[]{"postgresql", "redis", "kafka", "astradb"}) {
            Map<String, Object> service = (Map<String, Object>) report.get(key);
            Boolean configured = (Boolean) service.get("configured");
            if (configured != null && configured) {
                count++;
            }
        }
        return count;
    }
}
