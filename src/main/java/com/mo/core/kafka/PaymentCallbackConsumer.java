package com.mo.core.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mo.core.events.PaymentCallbackEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "app.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class PaymentCallbackConsumer {

    private final ObjectMapper objectMapper;
    private final com.mo.core.services.OrganisationService organisationService;

    @KafkaListener(topics = "payment-hold-callback", groupId = "payment-callback-consumer")
    public void onHoldCallback(String message) {
        handleMessage(message);
    }

    @KafkaListener(topics = "payment-release-callback", groupId = "payment-callback-consumer")
    public void onReleaseCallback(String message) {
        handleMessage(message);
    }

    @KafkaListener(topics = "payment-refund-callback", groupId = "payment-callback-consumer")
    public void onRefundCallback(String message) {
        handleMessage(message);
    }

    private void handleMessage(String message) {
        try {
            PaymentCallbackEvent evt = objectMapper.readValue(message, PaymentCallbackEvent.class);
            log.info("Received payment callback: {}", evt);
            organisationService.processPaymentCallback(evt.getTransactionRef(), evt.getStatus(), evt.getAction(), evt.getAmount(), evt.getReason());
        } catch (Exception e) {
            log.error("Failed to process payment callback message", e);
        }
    }
}
