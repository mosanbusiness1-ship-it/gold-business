package com.mo.configuration;

import com.mo.core.events.OrganisationProductValidationEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka configuration for organisation validation events.
 * Configures producer and consumer for async moderation workflow.
 */
@Configuration
@EnableKafka
@ConditionalOnProperty(prefix = "app.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${spring.kafka.security.protocol:PLAINTEXT}")
    private String securityProtocol;

    @Value("${spring.kafka.sasl.mechanism:PLAIN}")
    private String saslMechanism;

    @Value("${spring.kafka.sasl.jaas.config:}")
    private String saslJaasConfig;

    /**
     * Producer factory for serializing organisation validation events to JSON
     */
    @Bean
    public ProducerFactory<String, OrganisationProductValidationEvent> validationProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, 
            org.apache.kafka.common.serialization.StringSerializer.class);
        configProps.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, 
            JsonSerializer.class);
        configProps.put(org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(org.apache.kafka.clients.producer.ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(org.apache.kafka.clients.producer.ProducerConfig.LINGER_MS_CONFIG, 10);
        addSecurityProperties(configProps);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Kafka template for sending validation events
     */
    @Bean
    public KafkaTemplate<String, OrganisationProductValidationEvent> validationKafkaTemplate() {
        return new KafkaTemplate<>(validationProducerFactory());
    }

    /**
     * Producer factory for payment request events
     */
    @Bean
    public ProducerFactory<String, com.mo.core.events.PaymentRequestEvent> paymentProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, 
            org.apache.kafka.common.serialization.StringSerializer.class);
        configProps.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, 
            JsonSerializer.class);
        configProps.put(org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(org.apache.kafka.clients.producer.ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(org.apache.kafka.clients.producer.ProducerConfig.LINGER_MS_CONFIG, 10);
        addSecurityProperties(configProps);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, com.mo.core.events.PaymentRequestEvent> paymentKafkaTemplate() {
        return new KafkaTemplate<>(paymentProducerFactory());
    }

    /**
     * Consumer factory for deserializing validation JSON events
     */
    @Bean
    public ConsumerFactory<String, OrganisationProductValidationEvent> organisationValidationConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG, 
            "org-moderation-consumer");
        configProps.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, 
            org.apache.kafka.common.serialization.StringDeserializer.class);
        configProps.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, 
            JsonDeserializer.class);
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.mo.core.events.OrganisationProductValidationEvent");
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        configProps.put(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configProps.put(org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        configProps.put(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);
        addSecurityProperties(configProps);

        return new DefaultKafkaConsumerFactory<>(configProps, 
            new org.apache.kafka.common.serialization.StringDeserializer(), 
            new ErrorHandlingDeserializer<>(new JsonDeserializer<>(OrganisationProductValidationEvent.class)));
    }

    /**
     * Kafka listener container factory for consuming organisation validation events
     */
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, OrganisationProductValidationEvent>> 
        organisationValidationListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrganisationProductValidationEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(organisationValidationConsumerFactory());
        factory.setConcurrency(3);
        return factory;
    }

    private void addSecurityProperties(Map<String, Object> configProps) {
        if (!"PLAINTEXT".equalsIgnoreCase(securityProtocol)) {
            configProps.put("security.protocol", securityProtocol);
            configProps.put("sasl.mechanism", saslMechanism);
            if (saslJaasConfig != null && !saslJaasConfig.isBlank()) {
                configProps.put("sasl.jaas.config", saslJaasConfig);
            }
        }
    }
}
