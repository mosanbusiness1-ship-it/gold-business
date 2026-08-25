package com.mo.core.services;
import com.mo.auth.JwtService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.transaction.annotation.Transactional;

import com.mo.auth.User;
// removed unused import JwtException
import com.mo.core.dtos.OrganisationSearchCriteria;
import com.mo.core.dtos.ProductFilter;
import com.mo.core.dtos.organisationsDtos.CreateOrganisationDTO;
// removed unused import OrganisationDTO
import com.mo.core.dtos.organisationsDtos.OrganisationStats;
import com.mo.core.dtos.organisationsDtos.OrganisationReviewDTO;
import com.mo.core.dtos.organisationsDtos.GuaranteePolicyDTO;
import com.mo.core.dtos.organisationsDtos.CommissionConfigDTO;
import com.mo.core.dtos.organisationsDtos.OrganisationProductMetaDTO;
import com.mo.core.enums.MemberStatus;
import com.mo.core.enums.MemberType;
import com.mo.core.enums.OrganisationStatus;
import com.mo.core.enums.CommissionMode;
import com.mo.core.enums.ProductApprovalStatus;
import com.mo.core.enums.OrganisationReviewStatus;
import com.mo.core.exceptions.OrganisationNotFoundException;
import com.mo.core.model.needs.AbstractUserNeed;
import com.mo.core.model.organisations.Organisation;
import com.mo.core.model.organisations.OrganisationProductReview;
import com.mo.core.model.organisations.OrganisationReview;
import com.mo.core.model.organisations.OrganisationMember;
import com.mo.core.model.organisations.OrganisationProductMeta;
import com.mo.core.model.organisations.OrganisationRatingSummary;
import com.mo.core.model.organisations.GuaranteePolicy;
import com.mo.core.model.organisations.CommissionTransaction;
import com.mo.core.model.products.AbstractProduct;
import com.mo.core.model.products.ElectronicProduct;
import com.mo.core.model.products.FashionProduct;
import com.mo.core.model.products.FoodProduct;
import com.mo.core.model.products.RealEstateProduct;
import com.mo.core.model.products.ServiceProduct;
import com.mo.core.model.products.VehicleProduct;
import com.mo.core.repositories.jpa.OrganisationMemberRepository;
import com.mo.core.repositories.jpa.OrganisationRepository;
import com.mo.core.repositories.jpa.OrganisationProductReviewRepository;
import com.mo.core.repositories.jpa.OrganisationProductMetaRepository;
import com.mo.core.repositories.jpa.OrganisationRatingSummaryRepository;
import com.mo.core.repositories.jpa.OrganisationReviewRepository;
import com.mo.core.repositories.jpa.GuaranteePolicyRepository;
import com.mo.core.repositories.jpa.CommissionTransactionRepository;
import com.mo.core.repositories.jpa.ProductRepository;
import com.mo.core.kafka.OrganisationValidationProducer;
import com.mo.core.events.OrganisationProductValidationEvent;
import com.mo.mappers.organisationMappers.CreateOrganisationMapper;
import com.mo.mappers.productsMappers.*;
import com.mo.repositories.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import jakarta.persistence.EntityNotFoundException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
// removed unused Collectors import
import java.util.ArrayList;

@Service
@Transactional
public class OrganisationService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OrganisationService.class);

    private final OrganisationRepository organisationRepository;
    private final OrganisationMemberRepository organisationMemberRepository;
    private final OrganisationProductReviewRepository productReviewRepository;
    private final OrganisationProductMetaRepository productMetaRepository;
    private final OrganisationRatingSummaryRepository ratingSummaryRepository;
    private final OrganisationReviewRepository organisationReviewRepository;
    private final GuaranteePolicyRepository guaranteePolicyRepository;
        private final com.mo.core.repositories.jpa.GuaranteeClaimRepository guaranteeClaimRepository;
    private final CommissionTransactionRepository commissionTransactionRepository;
    private final com.mo.core.repositories.jpa.EscrowTransactionRepository escrowTransactionRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    private final CreateOrganisationMapper organisationMapper;
    private final VehicleProductMapper vehicleMapper;
    private final FashionProductMapper fashionMapper;
    private final ElectronicProductMapper electronicMapper;
    private final RealEstateProductMapper realEstateMapper;
    private final ServiceProductMapper serviceMapper;
    private final FoodProductMapper foodMapper;
    private final JwtService jwtService;
    private final OrganisationValidationProducer validationProducer;
    private final com.mo.core.kafka.PaymentProducer paymentProducer;

    @org.springframework.beans.factory.annotation.Autowired
    public OrganisationService(OrganisationRepository organisationRepository, 
                             UserRepository userRepository,
                             ProductRepository productRepository,
                             CreateOrganisationMapper organisationMapper, 
                             ElectronicProductMapper electronicMapper, 
                             FashionProductMapper fashionMapper, 
                             VehicleProductMapper vehicleMapper,
                             FoodProductMapper foodMapper,
                             RealEstateProductMapper realEstateMapper,
                             ServiceProductMapper serviceMapper,
                             JwtService jwtService,
                             OrganisationMemberRepository organisationMemberRepository,
                             OrganisationProductReviewRepository productReviewRepository,
                             OrganisationProductMetaRepository productMetaRepository,
                             OrganisationRatingSummaryRepository ratingSummaryRepository,
                             OrganisationReviewRepository organisationReviewRepository,
                             GuaranteePolicyRepository guaranteePolicyRepository,
                             CommissionTransactionRepository commissionTransactionRepository,
                             OrganisationValidationProducer validationProducer,
                             com.mo.core.kafka.PaymentProducer paymentProducer,
                             com.mo.core.repositories.jpa.GuaranteeClaimRepository guaranteeClaimRepository,
                             com.mo.core.repositories.jpa.EscrowTransactionRepository escrowTransactionRepository) {
        this.organisationRepository = organisationRepository;
		this.organisationMemberRepository = organisationMemberRepository;
        this.productReviewRepository = productReviewRepository;
        this.productMetaRepository = productMetaRepository;
        this.ratingSummaryRepository = ratingSummaryRepository;
        this.organisationReviewRepository = organisationReviewRepository;
        this.guaranteePolicyRepository = guaranteePolicyRepository;
        this.guaranteeClaimRepository = guaranteeClaimRepository;
        this.commissionTransactionRepository = commissionTransactionRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.organisationMapper = organisationMapper;
        this.fashionMapper = fashionMapper;
        this.electronicMapper = electronicMapper;
        this.vehicleMapper = vehicleMapper;
        this.foodMapper = foodMapper;
        this.realEstateMapper = realEstateMapper;
        this.serviceMapper = serviceMapper;
        this.jwtService = jwtService;
        this.validationProducer = validationProducer;
        this.paymentProducer = paymentProducer;
        this.escrowTransactionRepository = escrowTransactionRepository;
    }

    /**
     * Backwards-compatible constructor used by some tests that don't provide GuaranteeClaimRepository.
     */
    public OrganisationService(OrganisationRepository organisationRepository, 
                              UserRepository userRepository,
                              ProductRepository productRepository,
                              CreateOrganisationMapper organisationMapper, 
                              ElectronicProductMapper electronicMapper, 
                              FashionProductMapper fashionMapper, 
                              VehicleProductMapper vehicleMapper,
                              FoodProductMapper foodProductMapper,
                              RealEstateProductMapper realEstateMapper,
                              ServiceProductMapper serviceMapper,
                              JwtService jwtService,
                              OrganisationMemberRepository organisationMemberRepository,
                              OrganisationProductReviewRepository productReviewRepository,
                              OrganisationProductMetaRepository productMetaRepository,
                              OrganisationRatingSummaryRepository ratingSummaryRepository,
                              OrganisationReviewRepository organisationReviewRepository,
                              GuaranteePolicyRepository guaranteePolicyRepository,
                              CommissionTransactionRepository commissionTransactionRepository,
                              OrganisationValidationProducer validationProducer,
                              com.mo.core.repositories.jpa.EscrowTransactionRepository escrowTransactionRepository) {
        this(organisationRepository, userRepository, productRepository, organisationMapper, electronicMapper, fashionMapper, vehicleMapper, foodProductMapper, realEstateMapper, serviceMapper, jwtService, organisationMemberRepository, productReviewRepository, productMetaRepository, ratingSummaryRepository, organisationReviewRepository, guaranteePolicyRepository, commissionTransactionRepository, validationProducer, null, null, escrowTransactionRepository);
    }

        // Use the injected OrganisationMapper instance
    public Organisation createOrganisation(CreateOrganisationDTO createOrganisationDTO) {
        User owner = userRepository.findById(createOrganisationDTO.getOwnerId())
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + createOrganisationDTO.getOwnerId()));
        Organisation organisation = organisationMapper.toEntity(createOrganisationDTO);
        organisation.setOwner(owner);
        organisation.setCreatedAt(LocalDateTime.now());
        
        if (organisation.getParent() != null && !organisationRepository.existsById(organisation.getParent().getId())) {
            throw new IllegalArgumentException("Parent organisation does not exist");
        }
        
        return organisationRepository.save(organisation);
    }
    
    public Organisation save(Organisation organisation) {
    	return organisationRepository.save(organisation);
    }

    @Transactional(readOnly = true)
    public Optional<Organisation> getOrganisationById(Long id) {
        return organisationRepository.findWithChildrenAndProductsById(id);
    }

    @Transactional(readOnly = true)
    public List<Organisation> getAllOrganisations() {
        List<Organisation> organisations = organisationRepository.findAll();

        return organisations;
    }

    @Transactional(readOnly = true)
    public Page<Organisation> getVerifiedOrganisations(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return organisationRepository.findByVerifiedTrue(pageable);
    }

   
    public Organisation findByToken(String token) {
        return organisationRepository.findByJoinToken(token)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organisation introuvable avec ce token"));
    }



    public Organisation updateOrganisation(Long id, Organisation organisationDetails) {
        Organisation organisation = organisationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Organisation not found with id: " + id));

        if (organisationDetails.getName() != null && !organisationDetails.getName().isBlank()) {
            organisation.setName(organisationDetails.getName());
        }
        if (organisationDetails.getType() != null) {
            organisation.setType(organisationDetails.getType());
        }
        if (organisationDetails.getCategory() != null && !organisationDetails.getCategory().isBlank()) {
            organisation.setCategory(organisationDetails.getCategory());
        }
        if (organisationDetails.getVisibility() != null) {
            organisation.setVisibility(organisationDetails.getVisibility());
        }
        if (organisationDetails.getLogoUrl() != null) {
            organisation.setLogoUrl(organisationDetails.getLogoUrl().isBlank() ? null : organisationDetails.getLogoUrl());
        }
        organisation.setUpdatedAt(LocalDateTime.now());

        return organisationRepository.save(organisation);
    }

    public void deleteOrganisation(Long id) {
        Organisation organisation = organisationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Organisation not found with id: " + id));
        
        if (!organisation.getChildren().isEmpty()) {
            throw new IllegalStateException("Cannot delete organisation with children. Delete children first.");
        }
        
        organisationRepository.delete(organisation);
    }

    // HIERARCHY MANAGEMENT
    @Transactional(readOnly = true)
    public List<Organisation> getFullHierarchy(Long organisationId) {
        return organisationRepository.findFullHierarchy(organisationId);
    }

    @Transactional(readOnly = true)
    public List<Organisation> getRootOrganisations() {
        return organisationRepository.findRootOrganisations();
    }

    public Organisation changeParent(Long organisationId, Long newParentId) {
        Organisation organisation = organisationRepository.findById(organisationId)
            .orElseThrow(() -> new EntityNotFoundException("Organisation not found"));
        
        if (newParentId == null) {
            organisation.setParent(null);
        } else {
            Organisation newParent = organisationRepository.findById(newParentId)
                .orElseThrow(() -> new EntityNotFoundException("Parent organisation not found"));
            
            if (organisation.getId().equals(newParent.getId())) {
                throw new IllegalArgumentException("Organisation cannot be its own parent");
            }
            
            organisation.setParent(newParent);
        }
        
        return organisationRepository.save(organisation);
    }

    // PRODUCT MANAGEMENT
    public Organisation addProductToOrganisation(Long organisationId, Long productId) {
        // Récupérer l'organisation
        Organisation organisation = organisationRepository.findById(organisationId)
            .orElseThrow(() -> new EntityNotFoundException("Organisation not found"));

        // Récupérer le produit
        AbstractProduct product = productRepository.findById(productId)
            .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        // Vérifier si le produit est déjà ajouté à l'organisation
        if (organisation.getProducts().stream().anyMatch(p -> p.getId().equals(productId))) {
            throw new IllegalStateException("Product already exists in organisation");
        }

        // Ajouter le produit à l'organisation
        organisation.getProducts().add(product);
        
        // Sauvegarder l'organisation mise à jour
        return organisationRepository.save(organisation);
    }

    public OrganisationProductReview assignOrganisationProductScore(
            Long organisationId,
            Long productId,
            int score,
            String comment,
            Long moderatorId) {

        Organisation organisation = organisationRepository.findById(organisationId)
            .orElseThrow(() -> new EntityNotFoundException("Organisation not found"));

        AbstractProduct product = productRepository.findById(productId)
            .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        boolean containsProduct = organisation.getProducts().stream()
            .anyMatch(p -> p.getId().equals(productId));

        if (!containsProduct) {
            throw new IllegalStateException("Product is not part of this organisation");
        }

        productReviewRepository.findByIdOrganisationIdAndIdProductId(organisationId, productId)
            .ifPresent(review -> {
                throw new IllegalStateException("Organisation score is already assigned and immutable");
            });

        OrganisationProductReview review = OrganisationProductReview.builder()
            .id(new com.mo.core.model.organisations.OrganisationProductReviewId(organisationId, productId))
            .organisation(organisation)
            .product(product)
            .orgScore(score)
            .comment(comment)
            .moderator(userRepository.findById(moderatorId)
                .orElseThrow(() -> new EntityNotFoundException("Moderator not found")))
            .build();

        OrganisationProductReview saved = productReviewRepository.save(review);

        // Upsert product meta
        var metaOpt = productMetaRepository.findByIdOrganisationIdAndIdProductId(organisationId, productId);
        OrganisationProductMeta meta = metaOpt.orElseGet(() -> OrganisationProductMeta.builder()
            .id(new com.mo.core.model.organisations.OrganisationProductMetaId(organisationId, productId))
            .organisation(organisation)
            .product(product)
            .build());

        meta.setOrgScore(score);
        productMetaRepository.save(meta);

        // Recompute organisation rating summary (average org score)
        var reviews = productReviewRepository.findByOrganisationId(organisationId);
        double avg = reviews.stream().mapToInt(OrganisationProductReview::getOrgScore).average().orElse(0.0);
        int count = reviews.size();

        OrganisationRatingSummary summary = ratingSummaryRepository.findById(organisationId)
            .orElseGet(() -> OrganisationRatingSummary.builder()
                .organisationId(organisationId)
                .organisation(organisation)
                .build());

        summary.setAverageOrgScore(avg);
        summary.setTotalProductsScored(count);
        ratingSummaryRepository.save(summary);

        return saved;
    }

    @Transactional(readOnly = true)
    public OrganisationProductReview getOrganisationProductReview(Long organisationId, Long productId) {
        return productReviewRepository.findByIdOrganisationIdAndIdProductId(organisationId, productId)
            .orElseThrow(() -> new EntityNotFoundException("Organisation review not found"));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getOrganisationRatingSummary(Long organisationId) {
        Organisation organisation = organisationRepository.findById(organisationId)
            .orElseThrow(() -> new EntityNotFoundException("Organisation not found"));

        OrganisationRatingSummary summary = ratingSummaryRepository.findById(organisationId)
            .orElseGet(() -> OrganisationRatingSummary.builder()
                .organisationId(organisationId)
                .organisation(organisation)
                .build());

        List<OrganisationReview> reviews = organisationReviewRepository.findByOrganisationId(organisationId);
        double averageCustomerScore = reviews.stream()
            .mapToInt(OrganisationReview::getRating)
            .average()
            .orElse(0.0);

        List<OrganisationProductReview> organisationProductReviews = productReviewRepository.findByOrganisationId(organisationId);
        double averageOrgScore = organisationProductReviews.stream()
            .mapToInt(OrganisationProductReview::getOrgScore)
            .average()
            .orElse(0.0);

        int totalProductsScored = organisationProductReviews.size();
        int totalCustomerReviews = reviews.size();
        int verifiedPurchaseReviews = (int) reviews.stream().filter(OrganisationReview::isVerifiedPurchase).count();

        summary.setAverageCustomerScore(averageCustomerScore);
        summary.setAverageOrgScore(averageOrgScore);
        summary.setTotalProductsScored(totalProductsScored);
        summary.setTotalCustomerReviews(totalCustomerReviews);
        summary.setVerifiedPurchaseReviews(verifiedPurchaseReviews);
        ratingSummaryRepository.save(summary);

        return Map.of(
            "organisationId", organisationId,
            "averageCustomerScore", averageCustomerScore,
            "averageOrgScore", averageOrgScore,
            "totalProductsScored", totalProductsScored,
            "totalCustomerReviews", totalCustomerReviews,
            "verifiedPurchaseReviews", verifiedPurchaseReviews
        );
    }

    public Organisation removeProductFromOrganisation(Long organisationId, Long productId) {
        Organisation organisation = organisationRepository.findById(organisationId)
            .orElseThrow(() -> new EntityNotFoundException("Organisation not found"));
        
        boolean removed = organisation.getProducts().removeIf(p -> p.getId().equals(productId));
        
        if (!removed) {
            throw new IllegalStateException("Product not found in organisation");
        }
        
        return organisationRepository.save(organisation);
    }

    @Transactional(readOnly = true)
    public List<AbstractProduct> getProductsByOrganisation(Long organisationId, ProductFilter filter) {
        Organisation organisation = organisationRepository.findById(organisationId)
            .orElseThrow(() -> new EntityNotFoundException("Organisation not found"));
        
        if (filter.getMinPrice() != null && filter.getType() != null) {
            return organisationRepository.findProductsByOrgAndTypeAndMinPrice(
                organisationId, 
                filter.getType(), 
                filter.getMinPrice()
            );
        } else if (filter.getMinPrice() != null) {
            return organisationRepository.findProductsByOrgAndMinPrice(organisationId, filter.getMinPrice());
        } else if (filter.getType() != null) {
            return organisationRepository.findProductsByOrgAndType(organisationId, filter.getType());
        }
        
        return new ArrayList<>(organisation.getProducts());
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<com.mo.core.model.organisations.OrganisationReview> getOrganisationReviews(
            Long organisationId,
            boolean verifiedOnly,
            int page,
            int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by("createdAt").descending());
        if (verifiedOnly) {
            return organisationReviewRepository.findVerifiedByOrganisationId(organisationId, pageable);
        }
        return organisationReviewRepository.findByOrganisationId(organisationId, pageable);
    }

    // Map OrganisationReview -> OrganisationReviewDTO for pagination responses
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<com.mo.core.dtos.organisationsDtos.OrganisationReviewDTO> getOrganisationReviewDtos(
            Long organisationId,
            boolean verifiedOnly,
            int page,
            int size) {

        var pageEntity = getOrganisationReviews(organisationId, verifiedOnly, page, size);

        return pageEntity.map(r -> com.mo.core.dtos.organisationsDtos.OrganisationReviewDTO.builder()
                .id(r.getId())
                .organisationId(r.getOrganisation() != null ? r.getOrganisation().getId() : null)
                .reviewerId(r.getReviewer() != null ? r.getReviewer().getId() : null)
                .rating(r.getRating())
                .title(r.getTitle())
                .comment(r.getComment())
                .isVerifiedPurchase(r.isVerifiedPurchase())
                .status(r.getStatus() != null ? r.getStatus().name() : null)
                .createdAt(r.getCreatedAt())
                .build());
    }

    public Object mapProductToDto(AbstractProduct product) {
        return switch (product.getType()) {
            case VEHICLE -> vehicleMapper.toDto((VehicleProduct) product);
            case FASHION -> fashionMapper.toDto((FashionProduct) product);
            case ELECTRONIC -> electronicMapper.toDto((ElectronicProduct) product);
            case FOOD -> foodMapper.toDto((FoodProduct) product);
            case REALESTATE-> realEstateMapper.toDto((RealEstateProduct) product);
            case SERVICE -> serviceMapper.toDto((ServiceProduct) product);
        };
    }



    // SEARCH AND FILTER
    @Transactional(readOnly = true)
    public List<Organisation> searchOrganisations(OrganisationSearchCriteria criteria) {
        return organisationRepository.searchOrganisations(
            criteria.getName(),
            criteria.getType(),
            criteria.getMinProductCount()
        );
    }

    // BULK OPERATIONS
    public int transferOwnership(Long fromUserId, Long toUserId) {
        if (!userRepository.existsById(toUserId)) {
            throw new EntityNotFoundException("Target user not found");
        }
        
        return organisationRepository.transferOwnership(fromUserId, toUserId);
    }

    // STATISTICS
    @Transactional(readOnly = true)
    public OrganisationStats getOrganisationStats(Long organisationId) {
        Organisation organisation = organisationRepository.findById(organisationId)
            .orElseThrow(() -> new OrganisationNotFoundException(organisationId));

        long productCount = Optional.ofNullable(organisation.getProducts())
            .map(p -> (long) p.size()).orElse(0L);

        long memberCount = Optional.ofNullable(organisation.getMemberships())
            .map(m -> (long) m.size()).orElse(0L);

        long childCount = Optional.ofNullable(organisation.getChildren())
            .map(c -> (long) c.size()).orElse(0L);

      

        LocalDateTime lastActivity = organisation.getMemberships().stream()
            .map(OrganisationMember::getModifiedAt)
            .max(LocalDateTime::compareTo)
            .orElse(organisation.getCreatedAt());

        return OrganisationStats.builder()
            .totalProducts(productCount)
            .activeMembers(memberCount)
            .childOrganisations(childCount)
            .lastActivityDate(lastActivity)
            .build();
    }
    
    
    
   public String generateInvitationToken(Long organisationId, Long currentUserId, String invitedEmail) {
    String invitationToken = jwtService.generateInvitationToken(
    	    organisationId,
    	    currentUserId, // l'inviteur
    	    invitedEmail,
    	    MemberType.FULL_MEMBER, // ou "ADMIN", selon le rôle qu’on veut proposer
    	    Duration.ofHours(48).toMillis() // durée de validité du lien
    	);
    return invitationToken ;
   }
   
   public void acceptInvitationToken(String token, Long userId) {
	   //@Value("${security.jwt.secret-key}")
	  String jwtSecret = "3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b";

        try {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
            .build()
            .parseClaimsJws(token)
            .getBody();

        Long organisationId = claims.get("organisationId", Long.class);

	        // Vérifie si l'organisation existe
	        Organisation organisation = organisationRepository.findById(organisationId)
	                .orElseThrow(() -> new EntityNotFoundException("Organisation non trouvée"));
	        // Vérifie si le membre existe déjà
	        boolean alreadyMember = organisationMemberRepository
	                .existsByOrganisationIdAndUserId(organisationId, userId);

	        if (alreadyMember) {
	            throw new IllegalStateException("Vous êtes déjà membre de cette organisation.");
	        }

	        // Ajoute le nouveau membre
	        OrganisationMember member = OrganisationMember.builder()
	                .organisation(organisation)
	                .user(userRepository.getReferenceById(userId))
	                .status(MemberStatus.ACTIVE)
	                .type(MemberType.FULL_MEMBER) // ou autre type par défaut
	                .roles(Set.of("FULL_MEMBER")) // ou autre rôle
	                .joinedAt(LocalDateTime.now())
	                .build();

	        organisationMemberRepository.save(member);

	    } catch (ExpiredJwtException e) {
	        throw new IllegalArgumentException("Le lien d'invitation a expiré.");
	    }   
	}

   public Organisation findById(Long organisationId) {
	    return organisationRepository.findById(organisationId)
	            .orElseThrow(() -> new EntityNotFoundException("Organisation not found with ID: " + organisationId));
	}


	@Transactional
	public List<Organisation> attachNeedToOrganisations(AbstractUserNeed need, List<Long> orgIds) {
	    List<Organisation> orgs = organisationRepository.findAllById(orgIds);
	
	    if (orgs.size() != orgIds.size()) {
	        throw new EntityNotFoundException("One or more organisations not found");
	    }
	
	    for (Organisation org : orgs) {
	        org.addNeed(need);  // méthode utilitaire bidirectionnelle
	    }
	
	    // Persist côté propriétaire
	    return organisationRepository.saveAll(orgs);
	}

	// ===== Phase 1: Trust Features - New Methods =====
	
	/**
	 * Add a review for the organisation (customer rating)
	 */
	public OrganisationReview addOrganisationReview(
	        Long organisationId,
	        Long userId,
	        Integer rating,
	        String title,
	        String comment,
	        boolean isVerifiedPurchase) {
	    
	    Organisation organisation = organisationRepository.findById(organisationId)
	        .orElseThrow(() -> new EntityNotFoundException("Organisation not found"));
	    
	    User reviewer = userRepository.findById(userId)
	        .orElseThrow(() -> new EntityNotFoundException("User not found"));
	    
	    if (rating < 1 || rating > 5) {
	        throw new IllegalArgumentException("Rating must be between 1 and 5");
	    }
	    
	    OrganisationReview review = OrganisationReview.builder()
	        .rating(rating)
	        .title(title)
	        .comment(comment)
	        .isVerifiedPurchase(isVerifiedPurchase)
	        .status(OrganisationReviewStatus.PUBLISHED)
	        .reviewer(reviewer)
	        .organisation(organisation)
	        .build();
	    
	    OrganisationReview saved = organisationReviewRepository.save(review);
	    
	    // Update RatingSummary
	    updateRatingSummary(organisationId);
	    
	    return saved;
	}
	
	/**
	 * Submit a product for validation by the organisation
	 * Emits PENDING event to Kafka for moderation workflow
	 */
	public OrganisationProductMeta submitProductForValidation(Long organisationId, Long productId) {
	    Organisation organisation = organisationRepository.findById(organisationId)
	        .orElseThrow(() -> new EntityNotFoundException("Organisation not found"));
	    
	    AbstractProduct product = productRepository.findById(productId)
	        .orElseThrow(() -> new EntityNotFoundException("Product not found"));
	    
	    boolean containsProduct = organisation.getProducts().stream()
	        .anyMatch(p -> p.getId().equals(productId));
	    
	    if (!containsProduct) {
	        throw new IllegalStateException("Product is not part of this organisation");
	    }
	    
	    // Create or update meta with PENDING status
	    var metaOpt = productMetaRepository.findByIdOrganisationIdAndIdProductId(organisationId, productId);
	    OrganisationProductMeta meta = metaOpt.orElseGet(() -> OrganisationProductMeta.builder()
	        .id(new com.mo.core.model.organisations.OrganisationProductMetaId(organisationId, productId))
	        .organisation(organisation)
	        .product(product)
	        .build());
	    
	    LocalDateTime now = LocalDateTime.now();
	    meta.setApprovalStatus(ProductApprovalStatus.PENDING);
	    meta.setSubmittedAt(now);
	    
	    OrganisationProductMeta saved = productMetaRepository.save(meta);
	    
	    // Emit PENDING event to Kafka
	    OrganisationProductValidationEvent event = OrganisationProductValidationEvent.builder()
	        .organisationId(organisationId)
	        .productId(productId)
	        .createdAt(now)
	        .eventType("PENDING")
	        .build();
	    
	    validationProducer.emitValidationPending(event);
	    
	    return saved;
	}
	
	/**
	 * Validate (approve/reject) a product submission
	 * Emits Kafka events for moderation workflow
	 */
    public void validateProduct(
            Long organisationId,
            Long productId,
            boolean approved,
            String comments,
            Long moderatorId) {

        User moderator = userRepository.findById(moderatorId)
            .orElseThrow(() -> new EntityNotFoundException("Moderator not found"));

        var key = new com.mo.core.model.organisations.OrganisationProductMetaId(organisationId, productId);
        OrganisationProductMeta meta = productMetaRepository.findById(key)
            .orElseThrow(() -> new EntityNotFoundException("ProductMeta not found"));

        LocalDateTime now = LocalDateTime.now();
        meta.setApprovalStatus(approved ? com.mo.core.enums.ProductApprovalStatus.APPROVED : com.mo.core.enums.ProductApprovalStatus.REJECTED);
        meta.setValidatedAt(now);
        meta.setValidatedBy(moderator);
        meta.setValidationComments(comments);

        // Calculate SLA: minutes elapsed from submission to validation
        long slaMinutesElapsed = Duration.between(meta.getSubmittedAt(), now).toMinutes();
        meta.setSlaMinutesElapsed(slaMinutesElapsed);
        
        // Check if SLA exceeded (24 hours = 1440 minutes)
        boolean slaExceeded = slaMinutesElapsed > 1440;
        meta.setSlaExceeded(slaExceeded);

        productMetaRepository.save(meta);

        // Publication approval and product certification are distinct concerns.
        // Approval means the organisation accepts the product for publication.
        // Certification is a separate explicit action and should be managed by the dedicated certify endpoint.

        // Emit Kafka event based on approval status
        OrganisationProductValidationEvent event = OrganisationProductValidationEvent.builder()
            .organisationId(organisationId)
            .productId(productId)
            .moderatorId(moderatorId)
            .approved(approved)
            .comments(comments)
            .createdAt(now)
            .slaMinutesElapsed(slaMinutesElapsed)
            .slaExceeded(slaExceeded)
            .build();

        if (approved) {
            validationProducer.emitValidationApproved(event);
        } else {
            validationProducer.emitValidationRejected(event);
        }
    }
	
	/**
	 * Configure commission settings for the organisation
	 */
	public void configureCommission(
	        Long organisationId,
	        BigDecimal commissionOnPublish,
	        BigDecimal commissionOnSale,
	        CommissionMode commissionMode) {
	    
	    Organisation organisation = organisationRepository.findById(organisationId)
	        .orElseThrow(() -> new EntityNotFoundException("Organisation not found"));
	    
	    organisation.setCommissionOnPublish(commissionOnPublish);
	    organisation.setCommissionOnSale(commissionOnSale);
	    organisation.setCommissionMode(commissionMode);
	    
	    organisationRepository.save(organisation);
	}
	
	/**
	 * Configure guarantee policy for the organisation
	 */
	public GuaranteePolicy configureGuarantee(
	        Long organisationId,
	        Integer durationMonths,
	        BigDecimal cost,
	        String coverage,
	        String conditions) {
	    
	    Organisation organisation = organisationRepository.findById(organisationId)
	        .orElseThrow(() -> new EntityNotFoundException("Organisation not found"));
	    
	    GuaranteePolicy policy = GuaranteePolicy.builder()
	        .organisation(organisation)
	        .durationMonths(durationMonths)
	        .cost(cost)
	        .coverage(coverage)
	        .conditions(conditions)
	        .active(true)
	        .build();
	    
	    GuaranteePolicy saved = guaranteePolicyRepository.save(policy);
	    
	    // Update organisation flag
	    organisation.setOffersGuarantee(true);
	    organisationRepository.save(organisation);
	    
	    return saved;
	}

    /**
     * Create a guarantee claim for a product under an organisation's guarantee.
     */
    public com.mo.core.model.organisations.GuaranteeClaim createGuaranteeClaim(Long organisationId, Long productId, String reason) {
        Organisation organisation = organisationRepository.findById(organisationId)
            .orElseThrow(() -> new EntityNotFoundException("Organisation not found"));

        com.mo.core.model.organisations.GuaranteeClaim claim = com.mo.core.model.organisations.GuaranteeClaim.builder()
            .organisation(organisation)
            .productId(productId)
            .reason(reason)
            .resolved(false)
            .build();

        return guaranteeClaimRepository.save(claim);
    }

    public java.util.List<com.mo.core.model.organisations.GuaranteeClaim> listGuaranteeClaims(Long organisationId) {
        return guaranteeClaimRepository.findByOrganisationId(organisationId);
    }

    /**
     * Resolve a guarantee claim (mark resolved and add resolution notes).
     */
    public com.mo.core.model.organisations.GuaranteeClaim resolveGuaranteeClaim(Long claimId, String resolutionNotes, Long resolverId) {
        com.mo.core.model.organisations.GuaranteeClaim claim = guaranteeClaimRepository.findById(claimId)
            .orElseThrow(() -> new EntityNotFoundException("Guarantee claim not found"));

        claim.setResolved(true);
        claim.setResolutionNotes(resolutionNotes);
        // Optionally: record who resolved (not present on entity currently)
        com.mo.core.model.organisations.GuaranteeClaim saved = guaranteeClaimRepository.save(claim);

        // If this claim concerns a sale with an active escrow held, refund the escrow as part of resolution
        try {
            if (saved.getOrganisation() != null) {
                Long orgId = saved.getOrganisation().getId();
                java.util.List<com.mo.core.model.organisations.EscrowTransaction> escrows = escrowTransactionRepository.findByOrganisationId(orgId);
                // find matching HELD escrow for product
                var optHeld = escrows.stream()
                    .filter(e -> e.getProductId() != null && e.getProductId().equals(saved.getProductId()))
                    .filter(e -> e.getStatus() == com.mo.core.enums.CommissionTransactionStatus.HELD)
                    .findFirst();
                if (optHeld.isPresent()) {
                    com.mo.core.model.organisations.EscrowTransaction held = optHeld.get();
                    refundEscrow(held.getId(), "guarantee-claim-resolution: " + (resolutionNotes == null ? "" : resolutionNotes));
                }
            }
        } catch (Exception ex) {
            // swallow to avoid breaking claim resolution flow
        }

        return saved;
    }
	
	/**
	 * Update the organisation's rating summary based on all reviews
	 */
	private void updateRatingSummary(Long organisationId) {
	    List<OrganisationReview> reviews = organisationReviewRepository.findByOrganisationId(organisationId);
	    
	    if (reviews.isEmpty()) {
	        return; // No reviews yet
	    }
	    
	    double averageRating = reviews.stream()
	        .mapToInt(OrganisationReview::getRating)
	        .average()
	        .orElse(0.0);
	    
	    Organisation organisation = organisationRepository.findById(organisationId)
	        .orElseThrow(() -> new EntityNotFoundException("Organisation not found"));
	    
	    OrganisationRatingSummary summary = ratingSummaryRepository.findById(organisationId)
	        .orElseGet(() -> OrganisationRatingSummary.builder()
	            .organisationId(organisationId)
	            .organisation(organisation)
	            .build());
	    
	    summary.setAverageCustomerScore(averageRating);
	    summary.setTotalCustomerReviews(reviews.size());
	    summary.setVerifiedPurchaseReviews((int) reviews.stream().filter(OrganisationReview::isVerifiedPurchase).count());
	    
	    ratingSummaryRepository.save(summary);
	}

        /**
         * Get pending products for moderation (moderation queue).
         * Returns products sorted by submission time (oldest first).
         */
        public java.util.List<OrganisationProductMeta> getModerationQueue() {
            return productMetaRepository.findAll().stream()
                .filter(meta -> meta.getApprovalStatus() == ProductApprovalStatus.PENDING)
                .sorted((a, b) -> {
                    if (a.getSubmittedAt() == null) return 1;
                    if (b.getSubmittedAt() == null) return -1;
                    return a.getSubmittedAt().compareTo(b.getSubmittedAt());
                })
                .toList();
        }

        /**
         * Get SLA exceeded items (validation took > 24 hours).
         * Useful for monitoring and alerting.
         */
        public java.util.List<OrganisationProductMeta> getSlaExceededItems() {
            return productMetaRepository.findAll().stream()
                .filter(OrganisationProductMeta::isSlaExceeded)
                .toList();
        }

        /**
         * Create an escrow transaction for a sale and hold funds.
         */
        public com.mo.core.model.organisations.EscrowTransaction createEscrow(Long organisationId, Long productId, BigDecimal amount, String metadata) {
            return createEscrow(organisationId, productId, amount, metadata, null);
        }

        public com.mo.core.model.organisations.EscrowTransaction createEscrow(Long organisationId, Long productId, BigDecimal amount, String metadata, String currency) {
            Organisation org = organisationRepository.findById(organisationId)
                .orElseThrow(() -> new EntityNotFoundException("Organisation not found"));
            com.mo.core.model.organisations.EscrowTransaction escrow = com.mo.core.model.organisations.EscrowTransaction.builder()
                .organisation(org)
                .productId(productId)
                .amount(amount)
                .status(com.mo.core.enums.CommissionTransactionStatus.HELD)
                .metadata(metadata)
                .build();
            escrow = escrowTransactionRepository.save(escrow);

            // create a pending commission transaction linked to this escrow
            CommissionTransaction pending = CommissionTransaction.builder()
                .organisation(org)
                .productId(productId)
                .type(CommissionTransaction.CommissionTransactionType.SALE)
                .amount(amount)
                .status(com.mo.core.enums.CommissionTransactionStatus.PENDING)
                .transactionRef("escrow-" + escrow.getId())
                .build();
            commissionTransactionRepository.save(pending);

            String eventCurrency = currency == null || currency.isEmpty() ? "XOF" : currency;
            try {
                com.mo.core.events.PaymentRequestEvent event = com.mo.core.events.PaymentRequestEvent.builder()
                    .organisationId(org.getId())
                    .productId(productId)
                    .escrowId(escrow.getId())
                    .transactionRef("escrow-" + escrow.getId())
                    .amount(amount)
                    .currency(eventCurrency)
                    .callbackTopic("payment-hold-callback")
                    .action("HOLD")
                    .description("Hold funds for escrow transaction")
                    .build();
                if (paymentProducer != null) paymentProducer.emitPaymentRequest(event);
            } catch (Exception e) {
                // don't fail escrow creation if payment producer is not configured
                // log and continue
            }

            return escrow;
        }

        /**
         * Process payment gateway callback events and reconcile commission/escrow state.
         */
        public void processPaymentCallback(String transactionRef, String status, String action, BigDecimal amount, String reason) {
            if (transactionRef == null) return;
            CommissionTransaction existing = commissionTransactionRepository.findByTransactionRef(transactionRef);
            if (existing == null) {
                log.warn("Payment callback received for unknown transactionRef={}", transactionRef);
                return;
            }

            if ("SUCCESS".equalsIgnoreCase(status)) {
                if ("RELEASE".equalsIgnoreCase(action)) {
                    existing.setStatus(com.mo.core.enums.CommissionTransactionStatus.RELEASED);
                    commissionTransactionRepository.save(existing);
                } else if ("REFUND".equalsIgnoreCase(action)) {
                    existing.setStatus(com.mo.core.enums.CommissionTransactionStatus.REFUNDED);
                    commissionTransactionRepository.save(existing);
                } else if ("HOLD".equalsIgnoreCase(action)) {
                    existing.setStatus(com.mo.core.enums.CommissionTransactionStatus.HELD);
                    commissionTransactionRepository.save(existing);
                }
                // If reference references escrow-<id>, update escrow record state as well
                if (transactionRef.startsWith("escrow-")) {
                    try {
                        Long escrowId = Long.parseLong(transactionRef.substring("escrow-".length()));
                        com.mo.core.model.organisations.EscrowTransaction escrow = escrowTransactionRepository.findById(escrowId).orElse(null);
                        if (escrow != null) {
                            if ("RELEASE".equalsIgnoreCase(action)) {
                                escrow.setStatus(com.mo.core.enums.CommissionTransactionStatus.RELEASED);
                                escrow.setReleasedAt(java.time.LocalDateTime.now());
                            } else if ("REFUND".equalsIgnoreCase(action)) {
                                escrow.setStatus(com.mo.core.enums.CommissionTransactionStatus.REFUNDED);
                            } else if ("HOLD".equalsIgnoreCase(action)) {
                                escrow.setStatus(com.mo.core.enums.CommissionTransactionStatus.HELD);
                            }
                            escrowTransactionRepository.save(escrow);
                        }
                    } catch (NumberFormatException ex) {
                        log.warn("Invalid escrow id in transactionRef={}", transactionRef);
                    }
                }
            } else if ("FAILED".equalsIgnoreCase(status)) {
                existing.setStatus(com.mo.core.enums.CommissionTransactionStatus.DISPUTED);
                commissionTransactionRepository.save(existing);
                if (transactionRef.startsWith("escrow-")) {
                    try {
                        Long escrowId = Long.parseLong(transactionRef.substring("escrow-".length()));
                        com.mo.core.model.organisations.EscrowTransaction escrow = escrowTransactionRepository.findById(escrowId).orElse(null);
                        if (escrow != null) {
                            escrow.setStatus(com.mo.core.enums.CommissionTransactionStatus.DISPUTED);
                            escrowTransactionRepository.save(escrow);
                        }
                    } catch (NumberFormatException ex) {
                        log.warn("Invalid escrow id in transactionRef={}", transactionRef);
                    }
                }
            } else {
                log.info("Payment callback for {} with status {} (action={})", transactionRef, status, action);
            }
        }

        @Transactional(readOnly = true)
        public java.util.List<com.mo.core.model.organisations.EscrowTransaction> getEscrowTransactionsByOrganisation(Long organisationId) {
            return escrowTransactionRepository.findByOrganisationId(organisationId);
        }

        /**
         * Release escrow and create a commission transaction record.
         */
        public com.mo.core.model.organisations.EscrowTransaction releaseEscrow(Long escrowId, Long moderatorId) {
            com.mo.core.model.organisations.EscrowTransaction escrow = escrowTransactionRepository.findById(escrowId)
                .orElseThrow(() -> new EntityNotFoundException("Escrow not found"));
            escrow.setStatus(com.mo.core.enums.CommissionTransactionStatus.RELEASED);
            escrow.setReleasedAt(LocalDateTime.now());
            escrowTransactionRepository.save(escrow);
            // try to find the pending commission transaction linked to this escrow and mark it released
            String ref = "escrow-" + escrow.getId();
            CommissionTransaction existing = commissionTransactionRepository.findByTransactionRef(ref);
            if (existing != null) {
                existing.setStatus(com.mo.core.enums.CommissionTransactionStatus.RELEASED);
                commissionTransactionRepository.save(existing);
            } else {
                // fallback: create a released commission transaction if none exists
                CommissionTransaction tx = CommissionTransaction.builder()
                    .organisation(escrow.getOrganisation())
                    .productId(escrow.getProductId())
                    .type(CommissionTransaction.CommissionTransactionType.SALE)
                    .amount(escrow.getAmount())
                    .status(com.mo.core.enums.CommissionTransactionStatus.RELEASED)
                    .transactionRef(ref)
                    .build();
                commissionTransactionRepository.save(tx);
            }

            try {
                com.mo.core.events.PaymentRequestEvent event = com.mo.core.events.PaymentRequestEvent.builder()
                    .organisationId(escrow.getOrganisation().getId())
                    .productId(escrow.getProductId())
                    .escrowId(escrow.getId())
                    .transactionRef(ref)
                    .amount(escrow.getAmount())
                    .currency("XOF")
                    .callbackTopic("payment-release-callback")
                    .action("RELEASE")
                    .description("Release escrowed funds to organisation")
                    .build();
                if (paymentProducer != null) paymentProducer.emitPaymentRequest(event);
            } catch (Exception e) {
                // continue even if payment event cannot be emitted
            }

            return escrow;
        }

        /**
         * Refund escrow (in case of dispute) and create a refund commission record.
         */
        public com.mo.core.model.organisations.EscrowTransaction refundEscrow(Long escrowId, String reason) {
            com.mo.core.model.organisations.EscrowTransaction escrow = escrowTransactionRepository.findById(escrowId)
                .orElseThrow(() -> new EntityNotFoundException("Escrow not found"));
            escrow.setStatus(com.mo.core.enums.CommissionTransactionStatus.REFUNDED);
            escrow.setMetadata((escrow.getMetadata() == null ? "" : escrow.getMetadata() + "\n") + "refundReason:" + reason);
            escrow.setReleasedAt(LocalDateTime.now());
            escrowTransactionRepository.save(escrow);
            // record refund transaction
            CommissionTransaction tx = CommissionTransaction.builder()
                .organisation(escrow.getOrganisation())
                .productId(escrow.getProductId())
                .type(CommissionTransaction.CommissionTransactionType.SALE)
                .amount(escrow.getAmount())
                .status(com.mo.core.enums.CommissionTransactionStatus.REFUNDED)
                .transactionRef("refund-for-escrow-" + escrow.getId())
                .build();
            commissionTransactionRepository.save(tx);

            try {
                com.mo.core.events.PaymentRequestEvent event = com.mo.core.events.PaymentRequestEvent.builder()
                    .organisationId(escrow.getOrganisation().getId())
                    .productId(escrow.getProductId())
                    .escrowId(escrow.getId())
                    .transactionRef("refund-for-escrow-" + escrow.getId())
                    .amount(escrow.getAmount())
                    .currency("XOF")
                    .callbackTopic("payment-refund-callback")
                    .action("REFUND")
                    .description("Refund escrowed funds due to guarantee or dispute")
                    .build();
                if (paymentProducer != null) paymentProducer.emitPaymentRequest(event);
            } catch (Exception e) {
                // continue even if payment event cannot be emitted
            }

            return escrow;
        }

        public AbstractProduct certifyProduct(Long organisationId, Long productId, boolean certified) {
	    Organisation organisation = organisationRepository.findById(organisationId)
	        .orElseThrow(() -> new EntityNotFoundException("Organisation not found"));

	    AbstractProduct product = productRepository.findById(productId)
	        .orElseThrow(() -> new EntityNotFoundException("Product not found"));

	    boolean containsProduct = organisation.getProducts().stream()
	        .anyMatch(p -> p.getId().equals(productId));

	    if (!containsProduct) {
	        throw new IllegalStateException("Product is not part of this organisation");
	    }

	    product.setCertified(certified);
	    return productRepository.save(product);
	}
}