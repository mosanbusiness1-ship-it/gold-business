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

@Service
public class BidService {

    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public BidService(BidRepository bidRepository,
                      AuctionRepository auctionRepository,
                      ProductRepository productRepository,
                      UserRepository userRepository) {
        this.bidRepository = bidRepository;
        this.auctionRepository = auctionRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Bid placeBid(Long auctionId, Long productId, Long bidderId, BigDecimal amount) {
        Auction auction = auctionRepository.findByIdWithProducts(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Auction introuvable: " + auctionId));

        if (!auction.isOpen()) {
            throw new IllegalStateException("Cette enchère est fermée ou inactive.");
        }

        AbstractProduct product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produit introuvable: " + productId));

        User bidder = userRepository.findById(bidderId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + bidderId));

        Bid bid = new Bid();
        bid.setAuction(auction);
        bid.setProduct(product);
        bid.setBidder(bidder);
        bid.setAmount(amount);

        auction.getProducts().add(product);
        auctionRepository.save(auction);

        return bidRepository.save(bid);
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
