package com.mo.core.kafka;

import com.mo.core.events.PaymentRequestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentProducer {

    private final KafkaTemplate<String, PaymentRequestEvent> kafkaTemplate;

    @Value("${kafka.topics.payment-request:payment-request}")
    private String topicPaymentRequest;

    public void emitPaymentRequest(PaymentRequestEvent event) {
        String key = event.getTransactionRef() != null ? event.getTransactionRef() : (event.getOrganisationId() + "-" + event.getProductId());
        log.info("🔔 Emitting payment request for ref={} amount={}", event.getTransactionRef(), event.getAmount());
        kafkaTemplate.send(topicPaymentRequest, key, event)
            .whenComplete((res, ex) -> {
                if (ex == null) log.info("✅ Payment request sent: {}", key);
                else log.error("❌ Failed to send payment request {}", key, ex);
            });
    }
}
