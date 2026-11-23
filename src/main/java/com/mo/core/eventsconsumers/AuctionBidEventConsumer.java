package com.mo.core.eventsconsumers;

import com.mo.core.events.AuctionBidEvent;
import com.mo.core.services.AuctionLiveUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuctionBidEventConsumer {

    private final AuctionLiveUpdateService auctionLiveUpdateService;

    @EventListener
    public void consume(AuctionBidEvent event) {
        try {
            log.info("[KAFKA_AUCTION_BID] received event for auction {} amount {}", event.getAuctionId(), event.getAmount());
            auctionLiveUpdateService.broadcastAuctionSummary(event.getAuctionId());
        } catch (Exception e) {
            log.error("[KAFKA_AUCTION_BID] failed to process event {}", event, e);
        }
    }
}
