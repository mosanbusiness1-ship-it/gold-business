package com.mo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.datastax.astra.client.DataAPIClient;

/**
 * Configuration pour la connexion à DataStax Astra DB
 * Utilise les identifiants définis dans Service_Connection_Variables.md
 */
@Configuration
public class AstraDBConfig {

    @Value("${astra.db.api-endpoint}")
    private String apiEndpoint;

    @Value("${astra.db.application-token}")
    private String applicationToken;

    /**
     * Bean pour initialiser le client DataAPI Astra DB
     * 
     * @return DataAPIClient configuré avec le token et l'endpoint
     */
    @Bean
    public DataAPIClient dataAPIClient() {
        return new DataAPIClient(applicationToken);
    }

    // Note: We avoid exposing Astra Database strong-typed bean to prevent
    // compilation coupling with specific Astra DB package versions.
    // Use the injected `DataAPIClient` to obtain database instances at runtime.
}
