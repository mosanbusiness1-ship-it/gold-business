package com.mo.configuration.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    
    @Value("${spring.kafka.security.protocol:PLAINTEXT}")
    private String securityProtocol;
    
    @Value("${spring.kafka.sasl.mechanism:PLAIN}")
    private String saslMechanism;
    
    @Value("${spring.kafka.sasl.jaas.config:}")
    private String saslJaasConfig;
    
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        
        // Add security configuration only if not PLAINTEXT
        if (!"PLAINTEXT".equals(securityProtocol)) {
            configProps.put("security.protocol", securityProtocol);
            configProps.put("sasl.mechanism", saslMechanism);
            configProps.put("sasl.jaas.config", saslJaasConfig);
        }
        
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    
    @Bean
    public ProducerFactory<String, Object> objectProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        
        // Add security configuration only if not PLAINTEXT
        if (!"PLAINTEXT".equals(securityProtocol)) {
            configProps.put("security.protocol", securityProtocol);
            configProps.put("sasl.mechanism", saslMechanism);
            configProps.put("sasl.jaas.config", saslJaasConfig);
        }
        
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> objectKafkaTemplate() {
        return new KafkaTemplate<>(objectProducerFactory());
    }
}