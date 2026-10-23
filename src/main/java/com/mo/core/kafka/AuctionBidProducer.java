package com.mo.core.kafka;

import com.mo.core.events.AuctionBidEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuctionBidProducer {

    private final KafkaTemplate<String, Object> objectKafkaTemplate;

    @Value("${kafka.topics.auction-bid-events:auction-bid-events}")
    private String auctionBidEventsTopic;

    public AuctionBidProducer(KafkaTemplate<String, Object> objectKafkaTemplate) {
        this.objectKafkaTemplate = objectKafkaTemplate;
    }

    public void publishBidEvent(AuctionBidEvent event) {
        objectKafkaTemplate.send(auctionBidEventsTopic, String.valueOf(event.getAuctionId()), event);
        log.info("[KAFKA_AUCTION_BID] published event for auction {} with amount {}", event.getAuctionId(), event.getAmount());
    }
}
