package com.mo.core.kafkaServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mo.auth.User;
import com.mo.core.kafka.consumers.MessageConsumer;
import com.mo.core.kafka.producers.MessageProducer;
import com.mo.core.model.products.AbstractProduct;

import lombok.extern.slf4j.Slf4j;
import com.mo.core.dtos.ProductForUserNotification;
import com.mo.core.dtos.NotificationData;

@Slf4j
@Component
public class KafkaNotificationService {

    @Autowired private MessageProducer producer;
    @Autowired private ObjectMapper objectMapper;

    public void sendProductNotification(User user, AbstractProduct product, String topic) {
        try {
            ProductForUserNotification notification = new ProductForUserNotification(user.getId(), product);
            String payload = objectMapper.writeValueAsString(notification);

            producer.send(topic, payload)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Erreur Kafka pour Notification : {}", notification, ex);
                    } else {
                        log.info("✔ Notification envoyée : topic={}, partition={}, offset={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
        } catch (JsonProcessingException e) {
            log.error("Erreur de sérialisation Notification : {}", product, e);
        }
    }

    public void sendSimilarMatchNotification(User user, AbstractProduct product) {
        try {
            NotificationData data = new NotificationData();
            data.setUserName(user.getName());
            data.setUserEmail(user.getEmail());
            data.setUserPhoneNumber(user.getPhoneNumber());
            data.setProductName(product.getName());
            data.setProductAmount(product.getPrice());
            data.setProductCurrency(product.getCurrency());
            data.setProductQuantity(product.getQuantity());

            producer.send("nytify-user-for-similarMatches", objectMapper.writeValueAsString(data));
            log.info("Sent notification to user {} for product {}", user.getId(), product.getId());

        } catch (Exception e) {
            log.error("Error sending notification to user: {}", user.getId(), e);
        }
    }
}

