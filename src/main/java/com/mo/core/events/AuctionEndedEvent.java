package com.mo.core.events;

import org.springframework.context.ApplicationEvent;

import com.mo.core.model.auctions.Auction;

public class AuctionEndedEvent extends ApplicationEvent {

    private final Auction auction;

    public AuctionEndedEvent(Object source, Auction auction) {
        super(source);
        this.auction = auction;
    }

    public Auction getAuction() {
        return auction;
    }
}

