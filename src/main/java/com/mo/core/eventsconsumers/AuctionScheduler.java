package com.mo.core.eventsconsumers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mo.core.events.AuctionEndedEvent;
import com.mo.core.model.auctions.Auction;
import com.mo.core.services.AuctionService;

@Component
public class AuctionScheduler {

    private final AuctionService auctionService;
    private final ApplicationEventPublisher eventPublisher;

    public AuctionScheduler(AuctionService auctionService, ApplicationEventPublisher eventPublisher) {
        this.auctionService = auctionService;
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(fixedDelay = 60000) // toutes les 60 secondes
    public void checkEndedAuctions() {
        List<Auction> endedAuctions = auctionService.getAuctionsEndingBefore(LocalDateTime.now());

        for (Auction auction : endedAuctions) {
            // Publier un événement pour déclencher le listener
            eventPublisher.publishEvent(new AuctionEndedEvent(this, auction));
        }
    }
}

