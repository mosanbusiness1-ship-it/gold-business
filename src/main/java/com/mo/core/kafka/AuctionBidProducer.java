package com.mo.core.kafka;

import com.mo.core.events.AuctionBidEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(value = "app.kafka.enabled", havingValue = "true", matchIfMissing = false)
public class AuctionBidProducer {

    private final KafkaTemplate<String, AuctionBidEvent> kafkaTemplate;

    @Value("${kafka.topics.auction-bid-events:auction-bid-events}")
    private String auctionBidEventsTopic;

    @Value("${app.kafka.enabled:false}")
    private boolean kafkaEnabled;

    public AuctionBidProducer(KafkaTemplate<String, AuctionBidEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishBidEvent(AuctionBidEvent event) {
        if (!kafkaEnabled) {
            log.warn("[KAFKA_AUCTION_BID] Kafka is disabled. Event not published for auction {}", event.getAuctionId());
            return;
        }

        kafkaTemplate.send(auctionBidEventsTopic,
                String.valueOf(event.getAuctionId()), event)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("[KAFKA_AUCTION_BID] failed to publish event for auction {}", event.getAuctionId(), ex);
                } else {
                    log.info("[KAFKA_AUCTION_BID] published event for auction {} partition={} offset={}",
                        event.getAuctionId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
                }
            });
    }
}
