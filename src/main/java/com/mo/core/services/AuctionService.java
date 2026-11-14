package com.mo.core.services;
import com.mo.auth.User;
import com.mo.core.dtos.CreateAuctionDTO;
import com.mo.core.dtos.auctions.AuctionRequest;
import com.mo.core.model.auctions.Auction;
import com.mo.core.model.needs.AbstractUserNeed;
import com.mo.core.model.organisations.Organisation;
import com.mo.core.model.products.AbstractProduct;
import com.mo.core.repositories.jpa.AuctionRepository;
import com.mo.core.repositories.jpa.ProductRepository;
import com.mo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final ProductRepository productRepository;
    private final UserNeedService userNeedService;
    private final OrganisationService organisationService;
    private final UserRepository userRepository;

    @Autowired
    public AuctionService(AuctionRepository auctionRepository, ProductRepository productRepository,
                          UserNeedService userNeedService, OrganisationService organisationService,
                          UserRepository userRepository) {
        this.auctionRepository = auctionRepository;
        this.productRepository = productRepository;
        this.userNeedService = userNeedService;
        this.organisationService = organisationService;
        this.userRepository = userRepository;
    }

    // Méthode utilitaire pour convertir Auction -> CreateAuctionDTO
    private CreateAuctionDTO toDTO(Auction auction) {
        AbstractUserNeed need = auction.getNeed();
        CreateAuctionDTO dto = new CreateAuctionDTO();
        dto.setId(auction.getId());
        dto.setOwnerId(need != null && need.getUser() != null ? need.getUser().getId() : null);
        dto.setActived(auction.isActived());
        dto.setStartedAt(auction.getStartedAt());
        dto.setEndAt(auction.getEndAt());
        dto.setNeedName(need != null ? need.getName() : null);
        dto.setMaxPrice(need != null ? need.getMaxPrice() : null);
        dto.setCurrency(need != null ? need.getCurrency() : null);
        dto.setNeedDescription(need != null ? need.getDescription() : null);
        dto.setNeedQuantity(need != null ? need.getQuantity() : 0);
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

    @Transactional
    public Auction createAuctionForOrganisation(Long orgId, AuctionRequest request, Long userId) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        if (request.getNeed() == null) {
            throw new IllegalArgumentException("Need payload is required");
        }

        Organisation organisation = organisationService.getOrganisationById(orgId)
                .orElseThrow(() -> new IllegalArgumentException("Organisation not found: " + orgId));
        if (organisation == null) {
            throw new IllegalArgumentException("Organisation not found: " + orgId);
        }

        if (request.getUserId() != null && !request.getUserId().equals(userId)) {
            throw new IllegalArgumentException("The userId in the payload does not match the authenticated user");
        }
        if (request.getNeed().getUserId() != null && !request.getNeed().getUserId().equals(userId)) {
            throw new IllegalArgumentException("The need user_id in the payload does not match the authenticated user");
        }

        request.setUserId(userId);
        request.getNeed().setUserId(userId);

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        AbstractUserNeed savedNeed = userNeedService.createFromDto(request.getNeed());
        savedNeed.setUser(currentUser);
        savedNeed = userNeedService.createNeed(savedNeed);

        organisationService.attachNeedToOrganisations(savedNeed, List.of(orgId));

        Auction auction = new Auction();
        auction.setStartedAt(request.getStartedAt());
        auction.setEndAt(request.getEndAt());
        auction.setActived(request.isActived());
        auction.setNeed(savedNeed);

        return auctionRepository.save(auction);
    }
}
