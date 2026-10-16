package com.mo.core.services;

import com.mo.core.dtos.auctions.AuctionSummaryDTO;
import com.mo.core.model.auctions.Auction;
import com.mo.core.model.auctions.Bid;
import com.mo.core.repositories.jpa.AuctionRepository;
import com.mo.core.repositories.jpa.BidRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionLiveUpdateService {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public AuctionSummaryDTO buildSummary(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Auction introuvable: " + auctionId));

        Bid highestBid = bidRepository.findByAuctionId(auctionId).stream()
                .max(Comparator.comparing(Bid::getAmount))
                .orElse(null);

        long bidCount = bidRepository.countByAuctionId(auctionId);
        BigDecimal currentHighestBid = highestBid != null ? highestBid.getAmount() : BigDecimal.ZERO;
        Long currentHighestBidderId = highestBid != null ? highestBid.getBidder().getId() : null;

        AuctionSummaryDTO dto = new AuctionSummaryDTO();
        dto.setAuctionId(auctionId);
        dto.setCurrentHighestBid(currentHighestBid);
        dto.setCurrentHighestBidderId(currentHighestBidderId);
        dto.setCurrentWinnerId(currentHighestBidderId);
        dto.setBidCount(bidCount);
        dto.setStatus(auction.isActived() ? "ACTIVE" : "CLOSED");
        dto.setEndsAt(auction.getEndAt());
        dto.setUpdatedAt(LocalDateTime.now());
        return dto;
    }

    public void broadcastAuctionSummary(Long auctionId) {
        AuctionSummaryDTO summary = buildSummary(auctionId);
        messagingTemplate.convertAndSend("/topic/auction." + auctionId, summary);
        log.info("[AUCTION_LIVE] broadcast summary for auction {}: highestBid={}, bidderId={}",
                auctionId, summary.getCurrentHighestBid(), summary.getCurrentHighestBidderId());
    }
}
