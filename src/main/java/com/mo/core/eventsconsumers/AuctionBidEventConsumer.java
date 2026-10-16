package com.mo.core.eventsconsumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mo.core.events.AuctionBidEvent;
import com.mo.core.services.AuctionLiveUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuctionBidEventConsumer {

    private final ObjectMapper objectMapper;
    private final AuctionLiveUpdateService auctionLiveUpdateService;

    @KafkaListener(topics = "auction-bid-events", groupId = "auction-live-consumer")
    public void consume(String payload) {
        try {
            AuctionBidEvent event = objectMapper.readValue(payload, AuctionBidEvent.class);
            log.info("[KAFKA_AUCTION_BID] received event for auction {} amount {}", event.getAuctionId(), event.getAmount());
            auctionLiveUpdateService.broadcastAuctionSummary(event.getAuctionId());
        } catch (Exception e) {
            log.error("[KAFKA_AUCTION_BID] failed to process event payload {}", payload, e);
        }
    }
}
