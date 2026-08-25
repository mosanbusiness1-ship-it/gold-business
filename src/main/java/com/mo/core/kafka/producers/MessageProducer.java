package com.mo.core.kafka.producers;

import java.util.concurrent.CompletableFuture;


import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Component
public class MessageProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    ObjectMapper objectMapper = new ObjectMapper();

    public MessageProducer() {
        this.kafkaTemplate = null;
    }

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    public MessageProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public <T> CompletableFuture<SendResult<String, String>> send(String topic, String payload) {
        if (kafkaTemplate == null) {
            log.warn("Kafka désactivé ou non configuré : impossible d’envoyer le message sur le topic {}", topic);
            return CompletableFuture.completedFuture(null);
        }
        //String payload = objectMapper.writeValueAsString(payload1);
        return kafkaTemplate.send(topic, payload)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("❌ Échec envoi topic={} payload={}", topic, payload, ex);
                    } else {
                        log.info("✔ Envoyé topic={}, partition={}, offset={}, payload={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset(),
                                payload);
                    }
                });
    }
}

