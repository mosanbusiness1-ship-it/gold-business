package com.mo.core.services;

import com.mo.auth.User;
import com.mo.core.model.auctions.Auction;
import com.mo.core.model.auctions.Bid;
import com.mo.core.repositories.jpa.AuctionRepository;
import com.mo.core.repositories.jpa.BidRepository;
import com.mo.core.repositories.jpa.ProductRepository;
import com.mo.repositories.UserRepository;
import com.mo.core.model.products.AbstractProduct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import com.mo.core.events.AuctionBidEvent;
import com.mo.core.kafka.AuctionBidProducer;
import com.mo.core.enums.OrganisationVisibility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;

@Service
public class BidService {

    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AuctionBidProducer auctionBidProducer;
    private final OrganisationMembershipService organisationMembershipService;

    @Autowired
    public BidService(BidRepository bidRepository,
                      AuctionRepository auctionRepository,
                      ProductRepository productRepository,
                      UserRepository userRepository,
                      AuctionBidProducer auctionBidProducer,
                      OrganisationMembershipService organisationMembershipService) {
        this.bidRepository = bidRepository;
        this.auctionRepository = auctionRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.auctionBidProducer = auctionBidProducer;
        this.organisationMembershipService = organisationMembershipService;
    }

    @Transactional
    public Bid placeBid(Long auctionId, Long productId, Long bidderId, BigDecimal amount) {
        Auction auction = auctionRepository.findByIdWithProducts(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Auction introuvable: " + auctionId));

        if (!auction.isOpen()) {
            throw new IllegalStateException("Cette enchère est fermée ou inactive.");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant de l'enchère doit être positif.");
        }

        User bidder = userRepository.findById(bidderId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + bidderId));

        enforceBidVisibilityRules(auction, bidder.getId());

        AbstractProduct product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produit introuvable: " + productId));

        Bid highestBid = bidRepository.findByAuctionId(auctionId).stream()
                .max((b1, b2) -> b1.getAmount().compareTo(b2.getAmount()))
                .orElse(null);

        if (highestBid != null && amount.compareTo(highestBid.getAmount()) <= 0) {
            throw new IllegalArgumentException("Le montant doit être strictement supérieur au prix actuel de l'enchère.");
        }

        Bid bid = new Bid();
        bid.setAuction(auction);
        bid.setProduct(product);
        bid.setBidder(bidder);
        bid.setAmount(amount);

        auction.getProducts().add(product);
        auctionRepository.save(auction);

        Bid savedBid = bidRepository.save(bid);

        AuctionBidEvent event = new AuctionBidEvent();
        event.setAuctionId(auctionId);
        event.setBidId(savedBid.getId());
        event.setBidderId(bidderId);
        event.setProductId(productId);
        event.setAmount(amount);
        event.setEventType("auction_bid_updated");
        event.setOccurredAt(java.time.LocalDateTime.now());
        auctionBidProducer.publishBidEvent(event);

        return savedBid;
    }

    private void enforceBidVisibilityRules(Auction auction, Long bidderId) {
        if (auction == null || auction.getNeed() == null || auction.getNeed().getOrganisations() == null || auction.getNeed().getOrganisations().isEmpty()) {
            return;
        }

        boolean hasRestrictedOrg = auction.getNeed().getOrganisations().stream()
                .filter(org -> org != null)
                .anyMatch(org -> org.getVisibility() == OrganisationVisibility.PROTECTED || org.getVisibility() == OrganisationVisibility.PRIVATE);

        if (!hasRestrictedOrg) {
            return;
        }

        boolean isMemberOfAnyRestrictedOrg = auction.getNeed().getOrganisations().stream()
                .filter(org -> org != null)
                .filter(org -> org.getVisibility() == OrganisationVisibility.PROTECTED || org.getVisibility() == OrganisationVisibility.PRIVATE)
                .anyMatch(org -> organisationMembershipService.isMember(bidderId, org.getId()));

        if (!isMemberOfAnyRestrictedOrg) {
            throw new AccessDeniedException("This auction is restricted to organisation members only.");
        }
    }

    public List<Bid> getBidsForAuction(Long auctionId) {
        return bidRepository.findByAuctionId(auctionId);
    }

    @Transactional
    public void markBidStatus(Long auctionId, Long winningProductId) {
        List<Bid> bids = bidRepository.findByAuctionId(auctionId);
        for (Bid bid : bids) {
            if (bid.getProduct().getId().equals(winningProductId)) {
                bid.setStatus(com.mo.core.model.auctions.BidStatus.WON);
            } else {
                bid.setStatus(com.mo.core.model.auctions.BidStatus.LOST);
            }
            bidRepository.save(bid);
        }
    }
}
