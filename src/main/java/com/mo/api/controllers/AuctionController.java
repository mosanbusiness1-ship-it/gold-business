package com.mo.api.controllers;

import com.mo.core.model.auctions.Auction;
import com.mo.auth.User;
import com.mo.core.dtos.CreateAuctionDTO;
import com.mo.core.dtos.auctions.AuctionRequest;
import com.mo.core.model.products.AbstractProduct;
import com.mo.core.dtos.productsDtos.AbstractProductDto;
import com.mo.core.dtos.productsDtos.ServiceProductDto;
import com.mo.core.dtos.productsDtos.VehicleProductDto;
import com.mo.core.dtos.productsDtos.ElectronicProductDto;
import com.mo.core.dtos.productsDtos.FashionProductDto;
import com.mo.core.dtos.productsDtos.FoodProductDto;
import com.mo.core.dtos.productsDtos.RealEstateProductDto;
import com.mo.core.model.needs.AbstractUserNeed;
import com.mo.core.dtos.auctions.BidResponseDTO;
import com.mo.core.dtos.auctions.CreateBidRequest;
import com.mo.core.model.auctions.Bid;
import com.mo.core.services.AuctionService;
import com.mo.core.services.BidService;
import com.mo.core.services.ProductService;
import com.mo.core.services.UserNeedService;
import com.mo.core.visitors.product_visitors.ProductVisitor;
import com.mo.core.visitors.product_visitors.ProductVisitorRegistry;
import com.mo.mappers.needMappers.NeedMapperJackson;
import com.mo.mappers.productsMappers.ProductMapperJackson;
import com.mo.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/public/auctions")
public class AuctionController {

    private final AuctionService auctionService;
    private final BidService bidService;
    private final UserNeedService needService;
    private final ProductVisitor<Double> qualityEvaluationVisitor;
    private final ProductService productService;
    private final ProductVisitorRegistry registry;
    private final ProductMapperJackson mapperVisitor;
    private final NeedMapperJackson needmapper;
    private final UserRepository userRepository;
    

    @Autowired
    public AuctionController(AuctionService auctionService, BidService bidService, ProductVisitor<Double> qualityEvaluationVisitor, ProductService service, ProductVisitorRegistry registry, ProductService productService, ProductMapperJackson mapperVisitor, UserNeedService needService, NeedMapperJackson needmapper, UserRepository userRepository) {
        this.auctionService = auctionService;
        this.bidService = bidService;
	this.needService = needService;
        this.qualityEvaluationVisitor = qualityEvaluationVisitor;
	this.productService = productService;
	this.registry = registry;
	this.mapperVisitor = mapperVisitor;
	this.needmapper = needmapper;
	this.userRepository = userRepository;
    }

    // 1️⃣ Create an auction
    @PostMapping
    @Operation(
        summary = "Create an auction",
        description = "Create an auction together with its underlying need",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Auction request example",
                        value = "{\"startedAt\": \"2026-08-01T09:00:00.000Z\", \"endAt\": \"2026-08-10T18:00:00.000Z\", \"isActived\": true, \"userId\": 1, \"need\": {\"name\": \"Recherche smartphone reconditionné\", \"max_price\": 200000, \"currency\": \"XAF\", \"quantity\": 1, \"type\": \"ELECTRONIC\", \"description\": \"Smartphone 4G minimum, bon état\", \"user_id\": 1, \"photo_urls\": [], \"mandatory_fields\": [\"brand\", \"model\"], \"electronic_type\": \"PHONE\", \"brand\": \"Apple\", \"model\": \"iPhone 11\", \"specifications\": \"{ \\\"min_ram\\\": \\\"4GB\\\" }\", \"warranty_period\": \"6\"}}"
                    )
                }
            )
        )
    )
    public ResponseEntity<CreateAuctionDTO> createAuction(@RequestBody AuctionRequest request) {
        // 1. Create the need from the incoming DTO
        AbstractUserNeed savedNeed = needService.createFromDto(request.getNeed());
        User user = userRepository.findById(request.getUserId()).get();
        savedNeed.setUser(user);
        savedNeed = needService.createNeed(savedNeed);
        
  
        // 2. Créer Auction avec lien vers Need
        Auction auction = new Auction();
        auction.setStartedAt(request.getStartedAt());
        auction.setEndAt(request.getEndAt());
        auction.setActived(request.isActived());
        auction.setNeed(savedNeed);
        
        System.out.println("apressssssssssss"
        		+ ""
        		+ ""
        		+ ""+savedNeed);
        

        CreateAuctionDTO savedAuction = auctionService.save(auction);

        return ResponseEntity.ok(savedAuction);
    }



    // 2️⃣ Supprimer une enchère
    @DeleteMapping("/{auctionId}")
    @Operation(summary = "Delete auction", description = "Delete an auction by its ID")
    public ResponseEntity<Void> deleteAuction(@PathVariable Long auctionId) {
        Optional<Auction> auction = auctionService.findById(auctionId);
        if (auction.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        auctionService.delete(auctionId); // on peut adapter delete pour prendre Long
        return ResponseEntity.noContent().build();
    }

    // 3️⃣ Récupérer une enchère par son ID
    @GetMapping("/{auctionId}")
    @Operation(summary = "Get auction", description = "Return auction details for the given auction ID")
    public ResponseEntity<CreateAuctionDTO> getAuction(@PathVariable Long auctionId) {
    	Auction auction = auctionService.findById(auctionId)
    			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction not found"));
        return ResponseEntity.ok(toDTO(auction));
    }

    // 4️⃣ Récupérer toutes les enchères (avec pageable)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "List auctions",
        description = "Return a paginated list of auction DTOs",
        responses = @ApiResponse(
            responseCode = "200",
            description = "Paginated auctions",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CreateAuctionDTO.class)
            )
        )
    )
    public ResponseEntity<Page<CreateAuctionDTO>> getAllAuctions(Pageable pageable) {
        Page<Auction> auctions = auctionService.getAllAuctions(pageable);
        List<CreateAuctionDTO> dtos = auctions.stream()
                .map(this::toDTO)
                .toList();
        return ResponseEntity.ok(new PageImpl<>(dtos, auctions.getPageable(), auctions.getTotalElements()));
    }

    // 5️⃣ Récupérer les produits liés à une enchère
    @GetMapping("/{auctionId}/products")
    @Operation(
        summary = "Get auction products",
        description = "Return the auction products using product DTOs",
        responses = @ApiResponse(
            responseCode = "200",
            description = "List of product DTOs",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(
                    schema = @Schema(oneOf = {
                        ServiceProductDto.class,
                        VehicleProductDto.class,
                        ElectronicProductDto.class,
                        FashionProductDto.class,
                        FoodProductDto.class,
                        RealEstateProductDto.class
                    })
                ),
                examples = @ExampleObject(
                    value = "[{\"name\": \"iPhone 11 Pro\", \"price\": 150000, \"quantity\": 10, \"currency\": \"XAF\", \"type\": \"ELECTRONIC\", \"brand\": \"Apple\", \"model\": \"iPhone 11 Pro\"}]"
                )
            )
        )
    )
    public ResponseEntity<List<AbstractProductDto>> getProducts(@PathVariable Long auctionId) {
        List<AbstractProductDto> products = auctionService.getProducts(auctionId).stream()
                .map(mapperVisitor::mapToDtoObject)
                .toList();
        return ResponseEntity.ok(products);
    }

    // 5.1️⃣ Récupérer les bids d'une enchère
    @GetMapping("/{auctionId}/bids")
    @Operation(summary = "Get auction bids", description = "Return all bids placed on the auction")
    public ResponseEntity<List<BidResponseDTO>> getBids(@PathVariable Long auctionId) {
        List<Bid> bids = bidService.getBidsForAuction(auctionId);
        List<BidResponseDTO> response = bids.stream()
                .map(this::toBidResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    // 6️⃣ Placer une offre (bid) sur une enchère
    @PostMapping("/{auctionId}/bids")
    @Operation(summary = "Place bid", description = "Create a bid on the specified auction")
    public ResponseEntity<BidResponseDTO> placeBid(@PathVariable Long auctionId,
                                                   @RequestBody CreateBidRequest request) {
        Bid bid = bidService.placeBid(auctionId, request.getProductId(), request.getBidderId(), request.getAmount());
        return ResponseEntity.ok(toBidResponse(bid));
    }

    // 6.1️⃣ Ajouter un produit à une enchère existante
    public static record AddProductRequest(Long productId) {}

//    @PostMapping("/{auctionId}/products")
//    public ResponseEntity<CreateAuctionDTO> addProductToAuctionUsingIds(
//            @PathVariable Long auctionId,
//            @RequestBody AddProductRequest request
//    ) {
//        CreateAuctionDTO updated = auctionService.addProductToAuction(auctionId, request.productId());
//        return ResponseEntity.ok(updated);
//    }
//    
//    
    
    @Transactional
    @PostMapping("/{auctionId}/products")
    @Operation(
        summary = "Add a product to an auction",
        description = "Create a product from the provided DTO and attach it to an auction",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"name\": \"Toyota Corolla 2018\", \"description\": \"Voiture très propre\", \"price\": 1200000, \"quantity\": 1, \"currency\": \"XAF\", \"type\": \"VEHICLE\", \"enabled\": true, \"version\": 1, \"owner_id\": \"1\", \"photo_urls\": [\"https://example.com/photos/corolla.jpg\"], \"vehicle_type\": \"CAR\", \"make\": \"Toyota\", \"model\": \"Corolla\", \"manufacturing_year\": 2018, \"mileage\": 45000.0, \"fuel_type\": \"Essence\", \"color\": \"Blanc\", \"vin_number\": \"JTDBU4EE9AJ123456\"}"
                )
            )
        ),
        responses = @ApiResponse(
            responseCode = "200",
            description = "Updated auction DTO after adding the product",
            content = @Content(schema = @Schema(implementation = CreateAuctionDTO.class))
        )
    )
    public ResponseEntity<CreateAuctionDTO> addProductToAuctionUsingAbstractProduct(
            @PathVariable Long auctionId,
            @RequestBody AbstractProductDto productDto
    ) {
        log.info("➡️ Received request to create product for auction: {}", productDto);

        AbstractProduct savedProduct = productService.createFromDto(productDto);
        log.info("✅ Product created: id={}, type={}", savedProduct.getId(), savedProduct.getType());

        Auction updated = auctionService.addProductToAuction(auctionId, savedProduct.getId());
        return ResponseEntity.ok(toDTO(updated));
    }

    private BidResponseDTO toBidResponse(Bid bid) {
        BidResponseDTO response = new BidResponseDTO();
        response.setId(bid.getId());
        response.setAuctionId(bid.getAuction() != null ? bid.getAuction().getId() : null);
        response.setProductId(bid.getProduct() != null ? bid.getProduct().getId() : null);
        response.setBidderId(bid.getBidder() != null ? bid.getBidder().getId() : null);
        response.setAmount(bid.getAmount());
        response.setStatus(bid.getStatus());
        response.setCreatedAt(bid.getCreatedAt());
        return response;
    }

    // 7️⃣ Récupérer le produit gagnant d'une enchère inversée
    @GetMapping("/{auctionId}/winning-product")
    @Operation(summary = "Get winning product", description = "Return the winning product for the auction using configurable scoring")
    public ResponseEntity<AbstractProductDto> getWinningProduct(
            @PathVariable Long auctionId,
            @RequestParam(defaultValue = "0.6") double alpha) {

        List<AbstractProduct> products = auctionService.getProducts(auctionId);
        Auction auction = auctionService.findById(auctionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction not found"));

        BigDecimal maxPrice = auction.getNeed().getMaxPrice();

        AbstractProduct winningProduct = products.stream()
                .max(Comparator.comparingDouble(p -> {
                    double quality = p.accept(qualityEvaluationVisitor);
                    double priceScore = 1 - (p.getPrice().doubleValue() / maxPrice.doubleValue());
                    return alpha * priceScore + (1 - alpha) * quality;
                }))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No products found in auction"));

        auction.setActived(false);
        auctionService.save(auction); // mise à jour de l'état

        return ResponseEntity.ok(mapperVisitor.mapToDtoObject(winningProduct));
    }
    
    // Méthode utilitaire pour convertir Auction -> CreateAuctionDTO
    private CreateAuctionDTO toDTO(Auction auction) {
        AbstractUserNeed need = auction.getNeed();
        CreateAuctionDTO dto = new CreateAuctionDTO();
        dto.setActived(auction.isActived());
        dto.setStartedAt(auction.getStartedAt());
        dto.setEndAt(auction.getEndAt());
        dto.setNeedName(need.getName());
        dto.setMaxPrice(need.getMaxPrice());
        dto.setCurrency(need.getCurrency());
        dto.setNeedDescription(need.getDescription());
        return dto;
    }
}
