package com.mo.api.controller;

import com.mo.core.services.ServiceConnectionVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;

import java.util.Map;

/**
 * Endpoint de diagnostic pour vérifier la connectivité avec tous les services externes
 * Accessible à : GET /api/v1/health/services
 */
@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
@Slf4j
public class ServiceHealthController {

    private final ServiceConnectionVerificationService verificationService;

    /**
     * Vérifie toutes les connexions aux services externes
     * 
     * @return Rapport détaillé de la connectivité
     */
    @GetMapping("/services")
    @Operation(summary = "Check all services", description = "Return an availability report for all external dependencies")
    public ResponseEntity<Map<String, Object>> checkAllServices() {
        log.info("🔍 Checking all service connections...");
        Map<String, Object> report = verificationService.verifyAllConnections();
        return ResponseEntity.ok(report);
    }

    /**
     * Vérifie la connexion PostgreSQL/Neon
     */
    @GetMapping("/services/postgresql")
    @Operation(summary = "Check PostgreSQL", description = "Verify the PostgreSQL/Neon connection")
    public ResponseEntity<Map<String, Object>> checkPostgres() {
        Map<String, Object> result = verificationService.verifyPostgresConnection();
        return ResponseEntity.ok(result);
    }

    /**
     * Vérifie la connexion Redis/Upstash
     */
    @GetMapping("/services/redis")
    @Operation(summary = "Check Redis", description = "Verify the Redis/Upstash connection")
    public ResponseEntity<Map<String, Object>> checkRedis() {
        Map<String, Object> result = verificationService.verifyRedisConnection();
        return ResponseEntity.ok(result);
    }

    /**
     * Vérifie la connexion Kafka
     */
    @GetMapping("/services/kafka")
    @Operation(summary = "Check Kafka", description = "Verify the Kafka connection")
    public ResponseEntity<Map<String, Object>> checkKafka() {
        Map<String, Object> result = verificationService.verifyKafkaConnection();
        return ResponseEntity.ok(result);
    }

    /**
     * Vérifie la connexion Astra DB
     */
    @GetMapping("/services/astradb")
    @Operation(summary = "Check Astra DB", description = "Verify the Astra DB connection")
    public ResponseEntity<Map<String, Object>> checkAstraDB() {
        Map<String, Object> result = verificationService.verifyAstraDBConnection();
        return ResponseEntity.ok(result);
    }
}
