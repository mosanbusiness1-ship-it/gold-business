package com.mo.core.kafka;

import com.mo.core.events.PaymentRequestEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentProducer {

    private final KafkaTemplate<String, PaymentRequestEvent> kafkaTemplate;

    public PaymentProducer() {
        this.kafkaTemplate = null;
    }

    @Autowired(required = false)
    public PaymentProducer(KafkaTemplate<String, PaymentRequestEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void emitPaymentRequest(PaymentRequestEvent event) {
        if (kafkaTemplate == null) {
            log.warn("Kafka désactivé ou non configuré : paiement non envoyé pour ref={} amount={}",
                event.getTransactionRef(), event.getAmount());
            return;
        }
        String key = event.getTransactionRef() != null ? event.getTransactionRef() : (event.getOrganisationId() + "-" + event.getProductId());
        log.info("🔔 Emitting payment request for ref={} amount={}", event.getTransactionRef(), event.getAmount());
        kafkaTemplate.send(topicPaymentRequest, key, event)
            .whenComplete((res, ex) -> {
                if (ex == null) log.info("✅ Payment request sent: {}", key);
                else log.error("❌ Failed to send payment request {}", key, ex);
            });
    }
}
