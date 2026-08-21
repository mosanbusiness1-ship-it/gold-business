package com.mo.core.services;
import com.mo.core.dtos.CreateAuctionDTO;
import com.mo.core.model.auctions.Auction;
import com.mo.core.model.products.AbstractProduct;
import com.mo.core.repositories.jpa.AuctionRepository;
import com.mo.core.repositories.jpa.ProductRepository;
import com.mo.core.model.needs.AbstractUserNeed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final ProductRepository productRepository;

    @Autowired
    public AuctionService(AuctionRepository auctionRepository, ProductRepository productRepository) {
        this.auctionRepository = auctionRepository;
        this.productRepository = productRepository;
    }

    // Méthode utilitaire pour convertir Auction -> CreateAuctionDTO
    private CreateAuctionDTO toDTO(Auction auction) {
        AbstractUserNeed need = auction.getNeed();
        CreateAuctionDTO dto = new CreateAuctionDTO();
        dto.setOwnerId(need.getUser().getId());
        dto.setActived(auction.isActived());
        dto.setStartedAt(auction.getStartedAt());
        dto.setEndAt(auction.getEndAt());
        dto.setNeedName(need.getName());
        dto.setMaxPrice(need.getMaxPrice());
        dto.setCurrency(need.getCurrency());
        dto.setNeedDescription(need.getDescription());
        return dto;
    }

    public CreateAuctionDTO create(Auction auction) {
    	auction.setActived(true);
        Auction saved = auctionRepository.save(auction);
        return toDTO(saved);
    }

    public void delete(Long auctionId) {
        auctionRepository.deleteById(auctionId);
    }

    public Optional<Auction> findById(Long id) {
        return auctionRepository.findById(id);
    }

    // Récupérer toutes les enchères terminées mais toujours actives
    public List<Auction> getAuctionsEndingBefore(LocalDateTime dateTime) {
        return auctionRepository.findByEndAtBeforeAndIsActivedTrue(dateTime)
                .stream()
                .collect(Collectors.toList());
    }

    public AbstractUserNeed getNeed(Long auctionId) {
        return auctionRepository.findNeedByAuctionId(auctionId);
    }

    public List<AbstractProduct> getProducts(Long auctionId) {
        return auctionRepository.findByIdWithProducts(auctionId)
                .map(a -> new ArrayList<>(a.getProducts()))
                .orElseGet(ArrayList::new);
    }

    @Transactional
    public Auction addProductToAuction(Long auctionId, Long productId) {
        Auction auction = auctionRepository.findByIdWithProducts(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Auction introuvable: " + auctionId));

        if (!auction.isOpen()) {
            throw new IllegalStateException("Cette enchère est fermée ou inactive.");
        }

        AbstractProduct product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produit introuvable: " + productId));

        auction.getProducts().add(product);
        return auctionRepository.save(auction);
//        return toDTO(saved);
    }

    public CreateAuctionDTO save(Auction a) {
        Auction saved = auctionRepository.save(a);
        return toDTO(saved);
    }

    public Page<Auction> getAllAuctions(Pageable pageable) {
        return auctionRepository.findAll(pageable)
                ;
    }
}
