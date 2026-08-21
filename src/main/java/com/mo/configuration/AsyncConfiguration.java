package com.mo.configuration;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuration pour l'exécution asynchrone des observateurs d'événements
 * Permet aux listeners de notification de s'exécuter sur des threads séparés
 * pour éviter le blocage du thread principal de traitement
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfiguration {

    /**
     * Thread pool executor pour les tâches asynchrones
     * - CorePoolSize: 5 threads par défaut
     * - MaxPoolSize: 10 threads maximum
     * - QueueCapacity: 100 tâches en attente maximum
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-observer-");
        executor.initialize();
        
        log.info("✅ AsyncConfiguration initialized: corePoolSize=5, maxPoolSize=10, queueCapacity=100");
        return executor;
    }
}
