package com.mo.api.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.mo.auth.User;
import com.mo.core.dtos.AddProductToOrganisationRequest;
import com.mo.core.dtos.BulkNeedsForOrganisationRequest;
import com.mo.core.dtos.OrganisationSearchCriteria;
import com.mo.core.dtos.ProductFilter;
import com.mo.core.dtos.UserDTO;
import com.mo.core.dtos.organisationsDtos.CreateOrganisationDTO;
import com.mo.core.dtos.organisationsDtos.CreateOrganisationResponseDTO;
import com.mo.core.dtos.organisationsDtos.GuaranteePolicyRequestDTO;
import com.mo.core.dtos.organisationsDtos.EscrowCreateRequestDTO;
import com.mo.core.dtos.organisationsDtos.EscrowRefundRequestDTO;
import com.mo.core.dtos.organisationsDtos.EscrowTransactionDTO;
import com.mo.core.dtos.organisationsDtos.GuaranteeClaimRequestDTO;
import com.mo.core.dtos.organisationsDtos.GuaranteeClaimResolveRequestDTO;
import com.mo.core.dtos.organisationsDtos.OrganisationDTO;
import com.mo.core.dtos.organisationsDtos.OrganisationProductScoreRequest;
import com.mo.core.dtos.organisationsDtos.OrganisationStats;
import com.mo.core.dtos.organisationsDtos.OrganisationWithProductsDTO;
import com.mo.core.dtos.organisationsDtos.WebhookSubscriptionRequestDTO;
import com.mo.core.dtos.organisationsDtos.ProductValidationRequestDTO;
import com.mo.core.dtos.organisationsDtos.UpdatedOrganisationResponseDTO;
import com.mo.core.dtos.organisationsDtos.CommissionConfigDTO;
import com.mo.core.dtos.productsDtos.AbstractProductDto;
import com.mo.core.enums.MemberType;
import com.mo.core.enums.OrganisationType;
import com.mo.core.enums.ProductType;
import com.mo.core.model.needs.AbstractUserNeed;
import com.mo.core.model.organisations.Organisation;
import com.mo.core.model.products.AbstractProduct;
import com.mo.core.services.OrganisationService;

import com.mo.core.services.OrganisationMembershipService;
import com.mo.core.services.ProductService;
import com.mo.core.services.UserNeedService;
import com.mo.core.services.WebhookService;
import com.mo.core.visitors.need_visitors.UserNeedVisitor;
import com.mo.core.visitors.need_visitors.UserNeedVisitorRegistry;
import com.mo.mappers.needMappers.NeedMapperJackson;
import com.mo.mappers.organisationMappers.CreateOrganisationResponseMapper;
import com.mo.mappers.organisationMappers.OrgProductMapper;
import com.mo.mappers.productsMappers.ProductMapperJackson;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.mo.core.dtos.userNeedsDTO.AbstractUserNeedDto;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Organisations", description = "Organisation management and trust features (reviews, ratings, validation)")
@RequestMapping("/api/organisations")
@RequiredArgsConstructor
public class OrganisationController {
	
	private final UserNeedVisitorRegistry visitorRegistry;
	
	private final ProductService productService;
	
	private final UserNeedService userNeedService;

    private final OrganisationService organisationService;
    private final com.mo.core.services.WebhookService webhookService;
    
    private final OrganisationMembershipService organisationMembershipService;
    
    private final CreateOrganisationResponseMapper createOrganisationResponseMapper;
    
    private final OrgProductMapper orgProductMapper;
    
    private final ProductMapperJackson mapperVisitor;
    
    private final NeedMapperJackson needmapper;
    private final ObjectMapper objectMapper;
    
    private static final Logger log = LoggerFactory.getLogger(OrganisationController.class);
    
    // Creation endpoint accessible (default)
    @PostMapping
    @Operation(
        summary = "Create organisation",
        description = "Create a new organisation with provided owner and details",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Organisation creation payload",
            required = true,
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = CreateOrganisationDTO.class),
                examples = @ExampleObject(value = "{\"name\":\"GreenTech Retail\",\"type\":\"SALES_GROUP\",\"category\":\"electronics\",\"visibility\":\"PUBLIC\",\"public_join\":true,\"requires_approval\":false,\"restricted_to_admins_only\":false}")
            )
        )
    )
    public ResponseEntity<CreateOrganisationResponseDTO> createOrganisation(
            @RequestBody CreateOrganisationDTO createOrganisationDTO,
            @RequestParam @io.swagger.v3.oas.annotations.Parameter(description = "Owner user id", example = "1", required = true) Long ownerId) {
    	createOrganisationDTO.setOwnerId(ownerId);
        Organisation createdOrg = organisationService.createOrganisation(createOrganisationDTO);
        CreateOrganisationResponseDTO res = createOrganisationResponseMapper.toDto(createdOrg);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    
    @GetMapping("/verified")
    @Transactional(readOnly = true)
    @PreAuthorize("permitAll()")
    @Operation(summary = "List verified organisations", description = "Return a paginated list of verified organisations")
    public ResponseEntity<Page<CreateOrganisationResponseDTO>> getVerifiedOrganisations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/organisations/verified called: page={}, size={}", page, size);
        try {
            Page<Organisation> organisations = organisationService.getVerifiedOrganisations(page, size);
            log.info("GET /api/organisations/verified success: totalElements={}, totalPages={}",
                    organisations.getTotalElements(), organisations.getTotalPages());
            return ResponseEntity.ok(organisations.map(createOrganisationResponseMapper::toDto));
        } catch (Exception ex) {
            log.error("GET /api/organisations/verified failed: page={}, size={}", page, size, ex);
            throw ex;
        }
    }

    // List of organisations accessible to everyone
    @GetMapping("/all")
    @PreAuthorize("permitAll()")
    @Operation(summary = "List organisations", description = "Return all organisations visible to the caller")
    public ResponseEntity<List<CreateOrganisationResponseDTO>> getAll(){
        log.info("GET /api/organisations/all called");
        try {
            List<Organisation> orgs = organisationService.getAllOrganisations();
            log.info("GET /api/organisations/all success: count={}", orgs.size());
            List<CreateOrganisationResponseDTO> orgDtos = new ArrayList<>();
            orgs.forEach(org -> {
                CreateOrganisationResponseDTO orgDto = createOrganisationResponseMapper.toDto(org);
                orgDtos.add(orgDto);
               
            });
            return ResponseEntity.ok(orgDtos);
        } catch (Exception ex) {
            log.error("GET /api/organisations/all failed", ex);
            throw ex;
        }
    }

    // Get an organisation: protected
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get organisation", description = "Return a single organisation by ID for authorized users")
    public ResponseEntity<Organisation> getOrganisation(@PathVariable Long id) {
        return organisationService.getOrganisationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update an organisation: protected
    @PutMapping("/{id}")
    @PreAuthorize("@organisationSecurity.isAdminOfOrganisation(authentication, #id)")
    @Transactional
    @Operation(summary = "Update organisation", description = "Update the organisation details for an authorised administrator")
    public ResponseEntity<Organisation> updateOrganisation(
            @PathVariable Long id,
            @RequestBody Organisation organisationDetails) {
        Organisation updatedOrg = organisationService.updateOrganisation(id, organisationDetails);
        return ResponseEntity.ok(updatedOrg);
    }

    // Update organisation logo url: protected
    @PutMapping("/update-logo/{id}")
    @PreAuthorize("@organisationSecurity.isAdminOfOrganisation(authentication, #id)")
    @Transactional
    @Operation(summary = "Update organisation logo", description = "Update the organisation logo URL for an authorised administrator")
    public ResponseEntity<Organisation> updateLogoUrl(
            @PathVariable Long id,
            @RequestBody String logoUrl) {
        Organisation organisationDetails = organisationService.getOrganisationById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organisation not found"));
        organisationDetails.setLogoUrl(logoUrl);

        Organisation updatedOrg = organisationService.updateOrganisation(id, organisationDetails);
        return ResponseEntity.ok(updatedOrg);
    }

    // Verify an organisation: protected
    @PutMapping("/verify/{id}")
    @PreAuthorize("hasRole('ROOT') or hasRole('ADMIN')")
    @Transactional
    @Operation(summary = "Verify an organisation", description = "Update the organisation status to verified by the system administrator: the boolean variable verified is set to true")
    public ResponseEntity<Organisation> verifyOrganisation(@PathVariable Long id) {
        Organisation notVerifiedOrg = organisationService.getOrganisationById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organisation not found"));
        notVerifiedOrg.setVerified(true);
        Organisation verifiedOrg = organisationService.updateOrganisation(id, notVerifiedOrg);
        return ResponseEntity.ok(verifiedOrg);
    }

    // Delete: protected
    @DeleteMapping("/{id}")
    @PreAuthorize("@organisationSecurity.isAdminOfOrganisation(authentication, #id)")
    @Operation(summary = "Delete organisation", description = "Delete an organisation by ID for authorized administrators")
    public ResponseEntity<Void> deleteOrganisation(@PathVariable Long id) {
        organisationService.deleteOrganisation(id);
        return ResponseEntity.noContent().build();
    }

    // Hierarchy: protected
    @GetMapping("/{id}/hierarchy")
    @PreAuthorize("@organisationSecurity.isAdminOfOrganisation(authentication, #id)")
    @Operation(summary = "Get organisation hierarchy", description = "Return hierarchical tree information for the organisation")
    public ResponseEntity<List<Organisation>> getOrganisationHierarchy(@PathVariable Long id) {
        List<Organisation> hierarchy = organisationService.getFullHierarchy(id);
        return ResponseEntity.ok(hierarchy);
    }

    // Retrieve root organisations: accessible
    @GetMapping("/roots")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get root organisations", description = "Return top-level organisations with no parent")
    public ResponseEntity<List<Organisation>> getRootOrganisations() {
        List<Organisation> roots = organisationService.getRootOrganisations();
        return ResponseEntity.ok(roots);
    }

    // Get verified organisations (paged, accepts page and limit)
    @GetMapping("/verified/limit")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get verified organisations by page/limit", description = "Return a page of verified organisations; specify page and limit")
    public ResponseEntity<org.springframework.data.domain.Page<CreateOrganisationResponseDTO>> getVerifiedOrganisationsLimit(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        log.info("GET /api/organisations/verified/limit called: page={}, limit={}", page, limit);
        try {
            org.springframework.data.domain.Page<Organisation> organisations = organisationService.getVerifiedOrganisations(page, limit);
            log.info("GET /api/organisations/verified/limit success: totalElements={}, totalPages={}",
                    organisations.getTotalElements(), organisations.getTotalPages());
            return ResponseEntity.ok(organisations.map(createOrganisationResponseMapper::toDto));
        } catch (Exception ex) {
            log.error("GET /api/organisations/verified/limit failed: page={}, limit={}", page, limit, ex);
            throw ex;
        }
    }

    // Change parent: protected
    @PatchMapping("/{id}/parent")
    @PreAuthorize("@organisationSecurity.isAdminOfOrganisation(authentication, #id)")
    @Operation(summary = "Change organisation parent", description = "Update the parent organisation for an organisation")
    public ResponseEntity<Organisation> changeParent(
            @PathVariable Long id,
            @RequestParam(required = false) Long newParentId) {
        Organisation updatedOrg = organisationService.changeParent(id, newParentId);
        return ResponseEntity.ok(updatedOrg);
    }

    // PRODUCT MANAGEMENT
   

    @PostMapping("{orgId}/products")
    @PreAuthorize("@organisationSecurity.isAllowedToAddProduct(authentication, #orgId)")
        @Operation(summary = "Add product to organisation", description = "Attach an existing product to the organisation by id")
        public ResponseEntity<?> addProductToOrganisation(@PathVariable @io.swagger.v3.oas.annotations.Parameter(description = "Organisation id", example = "10") Long orgId,
            @RequestParam @io.swagger.v3.oas.annotations.Parameter(description = "Product id to attach", example = "451") Long productId) {
        log.info("→ Starting add product to organisation: orgId={}, productId={}", orgId, productId);

        // Ajout du produit à l'organisation
        Organisation org = organisationService.addProductToOrganisation(orgId, productId);
        log.info("✔ Product added to organisation");

        // Conversion des produits associés en DTOs
        Set<JsonNode> productDtos = org.getProducts()
            .stream()
            .map(mapperVisitor::mapToDto)
            .collect(Collectors.toSet());

        // Création d’un DTO de réponse combiné
        OrganisationWithProductsDTO responseDto = OrganisationWithProductsDTO.builder()
            .organisation(orgProductMapper.toDto(org))
            .products(productDtos)
            .build();

        log.info("✔ Converted organisation and products to DTO successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping("/{orgId}/products/{productId}/score")
    @PreAuthorize("@organisationSecurity.canModerateOrganisation(authentication, #orgId)")
    @Operation(summary = "Assign product score",
               description = "Moderator assigns a score/comment for an organisation's product",
               responses = {@ApiResponse(responseCode = "201", description = "Score created")})
    public ResponseEntity<?> assignOrganisationProductScore(
            @PathVariable Long orgId,
            @PathVariable Long productId,
            @RequestBody @Valid OrganisationProductScoreRequest request,
            @RequestAttribute("userId") Long moderatorId) {

        var review = organisationService.assignOrganisationProductScore(
            orgId,
            productId,
            request.getScore(),
            request.getComment(),
            moderatorId
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    @GetMapping("/{orgId}/products/{productId}/score")
    @PreAuthorize("@organisationSecurity.canAccessProducts(authentication, #orgId)")
    @Operation(summary = "Get product score",
               description = "Retrieve moderator score for a product in the organisation",
               responses = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<?> getOrganisationProductScore(
            @PathVariable Long orgId,
            @PathVariable Long productId) {

        var review = organisationService.getOrganisationProductReview(orgId, productId);
        return ResponseEntity.ok(review);
    }



   
    
    @DeleteMapping("/{orgId}/products/{productId}")
    @PreAuthorize("@organisationSecurity.isAdminOfOrganisation(authentication, #orgId)")
    @Operation(summary = "Remove product from organisation", description = "Remove a product from an organisation and return the updated organisation DTO")
    public ResponseEntity<OrganisationDTO> removeProductFromOrganisation(
            @PathVariable Long orgId,
            @PathVariable Long productId) {

        Organisation org = organisationService.removeProductFromOrganisation(orgId, productId);
        OrganisationDTO response = orgProductMapper.toDto(org);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/{orgId}/products")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get organisation products", description = "Return the paginated list of products for the organisation using DTO mapping")
    public ResponseEntity<?> getOrganisationProducts(
            @PathVariable Long orgId,
            @RequestParam(required = false) ProductType type,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {

        if (page < 0) {
            throw new IllegalArgumentException("Page index must be >= 0");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be > 0");
        }

        ProductFilter filter = new ProductFilter(type, minPrice);
        List<AbstractProduct> products = organisationService.getProductsByOrganisation(orgId, filter);

        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, products.size());

        if (fromIndex >= products.size()) {
            fromIndex = products.size();
            toIndex = products.size();
        }

        List<AbstractProduct> pagedProducts = products.subList(fromIndex, toIndex);
        List<JsonNode> result = pagedProducts.stream()
                .map(mapperVisitor::mapToDto)
                .collect(Collectors.toList());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("organisationId", orgId);
        response.put("page", page);
        response.put("size", size);
        response.put("totalElements", products.size());
        response.put("totalPages", products.isEmpty() ? 0 : (int) Math.ceil((double) products.size() / size));
        response.put("content", result);

        return ResponseEntity.ok(response);
    }

    // SEARCH AND STATISTICS
    @GetMapping("/search")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Search organisations", description = "Search organisations by name, type, or minimum product count")
    public ResponseEntity<List<Organisation>> searchOrganisations(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) OrganisationType type,
            @RequestParam(required = false) Long minProductCount) {
        OrganisationSearchCriteria criteria = 
                new OrganisationSearchCriteria(name, type, minProductCount);
        List<Organisation> results = organisationService.searchOrganisations(criteria);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}/stats")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get organisation stats", description = "Return aggregated statistics for the organisation")
    public ResponseEntity<OrganisationStats> getOrganisationStats(@PathVariable Long id) {
        OrganisationStats stats = organisationService.getOrganisationStats(id);
        return ResponseEntity.ok(stats);
    }

    // EXCEPTION HANDLER
    @ExceptionHandler({EntityNotFoundException.class, IllegalArgumentException.class})
    public ResponseEntity<String> handleBadRequests(Exception ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleConflict(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
    
    // ===== Phase 1: Trust Features - New Endpoints =====
    
    /**
     * POST /api/organisations/{orgId}/reviews - Add a review for the organisation
     */
    @PostMapping("/{orgId}/reviews")
    @PreAuthorize("@organisationSecurity.canAccessProducts(authentication, #orgId)")
    @Operation(summary = "Add organisation review",
               description = "Add a customer review for the organisation",
               requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                   description = "Review payload",
                   required = true,
                   content = @Content(mediaType = "application/json",
                       schema = @Schema(implementation = com.mo.core.dtos.organisationsDtos.OrganisationReviewRequest.class),
                       examples = @ExampleObject(value = "{\"rating\":5,\"title\":\"Excellent seller\",\"comment\":\"Fast delivery and clear communication.\",\"is_verified_purchase\":true}")
                   )
               ),
               responses = {@ApiResponse(responseCode = "201", description = "Created"), @ApiResponse(responseCode = "400", description = "Bad Request")})
    public ResponseEntity<?> addReview(
            @PathVariable Long orgId,
            @RequestBody com.mo.core.dtos.organisationsDtos.OrganisationReviewRequest request,
            @RequestAttribute("userId") Long userId) {
        
        try {
            var review = organisationService.addOrganisationReview(
                orgId,
                userId,
                request.getRating(),
                request.getTitle(),
                request.getComment(),
                request.isVerifiedPurchase()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(review);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    
    /**
     * GET /api/organisations/{orgId}/reviews - List reviews for the organisation
     */
    @GetMapping("/{orgId}/reviews")
    @PreAuthorize("permitAll()")
    @Operation(summary = "List organisation reviews",
               description = "List customer reviews for an organisation, optionally only verified purchases",
               responses = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<?> getReviews(
            @PathVariable Long orgId,
            @RequestParam(required = false, defaultValue = "false") boolean verifiedOnly,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {

        try {
            var reviewsPage = organisationService.getOrganisationReviewDtos(orgId, verifiedOnly, page, size);
            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("organisationId", orgId);
            resp.put("page", reviewsPage.getNumber());
            resp.put("size", reviewsPage.getSize());
            resp.put("totalElements", reviewsPage.getTotalElements());
            resp.put("totalPages", reviewsPage.getTotalPages());
            resp.put("content", reviewsPage.getContent());
            return ResponseEntity.ok(resp);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
    
    /**
     * GET /api/organisations/{orgId}/ratings - Get rating summary
     */
    @GetMapping("/{orgId}/ratings")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get rating summary",
               description = "Retrieve aggregated rating summary for the organisation",
               responses = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<?> getRatingSummary(@PathVariable Long orgId) {
        
        try {
            var summary = organisationService.getOrganisationRatingSummary(orgId);
            return ResponseEntity.ok(summary);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
    
    /**
     * POST /api/organisations/{orgId}/products/{productId}/submit - Submit product for validation
     */
    @PostMapping("/{orgId}/products/{productId}/submit")
    @PreAuthorize("@organisationSecurity.isOwnerOrAdmin(authentication, #orgId)")
    @Operation(summary = "Submit product for validation",
               description = "Owner or admin submits a product for moderation/validation",
               responses = {@ApiResponse(responseCode = "201", description = "Submitted")})
    public ResponseEntity<?> submitProductForValidation(
            @PathVariable Long orgId,
            @PathVariable Long productId) {
        
        try {
            var meta = organisationService.submitProductForValidation(orgId, productId);
            return ResponseEntity.status(HttpStatus.CREATED).body(meta);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    /**
     * POST /api/organisations/{orgId}/validateProduct/{productId} - Approve or reject a submitted product
     */
    @PostMapping("/{orgId}/validateProduct/{productId}")
    @PreAuthorize("@organisationSecurity.canModerateOrganisation(authentication, #orgId)")
    @Operation(summary = "Validate submitted product",
               description = "Moderator approves or rejects a submitted product",
               requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                   description = "Validation payload containing 'approved' boolean and optional 'comments'",
                   required = true,
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = ProductValidationRequestDTO.class),
                                      examples = {@ExampleObject(value = "{\"approved\":true,\"comments\":\"Looks good and meets the quality rules.\"}")})
               ),
               responses = {@ApiResponse(responseCode = "200", description = "Validation applied"), @ApiResponse(responseCode = "400", description = "Bad Request"), @ApiResponse(responseCode = "404", description = "Organisation or product not found")})
    public ResponseEntity<?> validateProduct(
            @PathVariable Long orgId,
            @PathVariable Long productId,
            @RequestBody @Valid ProductValidationRequestDTO request,
            @RequestAttribute("userId") Long moderatorId) {

        try {
            boolean approved = Boolean.TRUE.equals(request.getApproved());
            String comments = request.getComments();
            organisationService.validateProduct(orgId, productId, approved, comments, moderatorId);
            return ResponseEntity.ok(Map.of("message", "Validation applied"));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    
    /**
     * POST /api/organisations/{orgId}/commission/config - Configure commissions
     */
    @PostMapping("/{orgId}/commission/config")
    @PreAuthorize("@organisationSecurity.isAdminOfOrganisation(authentication, #orgId)")
    @Operation(summary = "Configure commission",
               description = "Set commission rates and mode for an organisation",
               requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                   description = "Commission configuration payload",
                   required = true,
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = CommissionConfigDTO.class),
                                      examples = {@ExampleObject(value = "{\"commission_on_publish\":500.00,\"commission_on_sale\":1200.00,\"commission_mode\":\"PERCENTAGE\"}")})
               ),
               responses = {@ApiResponse(responseCode = "200", description = "Configured"), @ApiResponse(responseCode = "404", description = "Organisation not found")})
    public ResponseEntity<?> configureCommission(
            @PathVariable Long orgId,
            @RequestBody CommissionConfigDTO request) {
        
        try {
            BigDecimal onPublish = request.getCommissionOnPublish();
            BigDecimal onSale = request.getCommissionOnSale();
            String modeStr = request.getCommissionMode();
            var mode = modeStr != null ? com.mo.core.enums.CommissionMode.valueOf(modeStr) : null;
            organisationService.configureCommission(orgId, onPublish, onSale, mode);
            return ResponseEntity.ok(Map.of("message", "Commission configured successfully"));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    
    /**
     * POST /api/organisations/{orgId}/guarantee - Configure guarantee policy
     */
    @PostMapping("/{orgId}/guarantee")
    @PreAuthorize("@organisationSecurity.isAdminOfOrganisation(authentication, #orgId)")
    @Operation(summary = "Configure guarantee policy",
               description = "Create or update a guarantee policy for the organisation",
               requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                   description = "Guarantee payload",
                   required = true,
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = GuaranteePolicyRequestDTO.class),
                                      examples = {@ExampleObject(value = "{\"duration_months\":12,\"cost\":1500.00,\"coverage\":\"Shipping damage and fraud protection\",\"conditions\":\"Return within 30 days with proof of issue.\"}")})
               ),
               responses = {@ApiResponse(responseCode = "201", description = "Created"), @ApiResponse(responseCode = "404", description = "Organisation not found")})
    public ResponseEntity<?> configureGuarantee(
            @PathVariable Long orgId,
            @RequestBody @Valid GuaranteePolicyRequestDTO request) {
        
        try {
            var policy = organisationService.configureGuarantee(
                orgId,
                request.getDurationMonths(),
                request.getCost(),
                request.getCoverage(),
                request.getConditions()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(policy);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/{orgId}/guarantee/claims")
    @Operation(summary = "Create guarantee claim",
               description = "Create a claim against a guarantee policy for an organisation product",
               requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                   description = "Guarantee claim payload",
                   required = true,
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = GuaranteeClaimRequestDTO.class),
                                      examples = {@ExampleObject(value = "{\"product_id\":451,\"reason\":\"Item arrived damaged.\"}")})
               ),
               responses = {@ApiResponse(responseCode = "201", description = "Created"), @ApiResponse(responseCode = "404", description = "Organisation not found")})
    public ResponseEntity<?> createGuaranteeClaim(
            @PathVariable Long orgId,
            @RequestBody @Valid GuaranteeClaimRequestDTO request) {
        try {
            var claim = organisationService.createGuaranteeClaim(orgId, request.getProductId(), request.getReason());
            return ResponseEntity.status(HttpStatus.CREATED).body(claim);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping("/{orgId}/guarantee/claims")
    @PreAuthorize("@organisationSecurity.isOwnerOrAdmin(authentication, #orgId)")
    public ResponseEntity<?> listGuaranteeClaims(@PathVariable Long orgId) {
        var claims = organisationService.listGuaranteeClaims(orgId);
        return ResponseEntity.ok(claims);
    }

    @PostMapping("/{orgId}/guarantee/claims/{claimId}/resolve")
    @PreAuthorize("@organisationSecurity.canModerateOrganisation(authentication, #orgId)")
    @Operation(summary = "Resolve guarantee claim",
               description = "Resolve an existing guarantee claim and attach resolution notes",
               requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                   description = "Guarantee claim resolution payload",
                   required = true,
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = GuaranteeClaimResolveRequestDTO.class),
                                      examples = {@ExampleObject(value = "{\"resolver_id\":10,\"notes\":\"Approved refund due to damaged item.\"}")})
               ),
               responses = {@ApiResponse(responseCode = "200", description = "Resolved"), @ApiResponse(responseCode = "404", description = "Claim not found")})
    public ResponseEntity<?> resolveGuaranteeClaim(
            @PathVariable Long orgId,
            @PathVariable Long claimId,
            @RequestBody @Valid GuaranteeClaimResolveRequestDTO request) {
        try {
            var resolved = organisationService.resolveGuaranteeClaim(claimId, request.getNotes(), request.getResolverId());
            return ResponseEntity.ok(resolved);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    // ===== Phase 3: Escrow & Commission Workflow =====

    @PostMapping("/{orgId}/escrow")
    @PreAuthorize("@organisationSecurity.isOwnerOrAdmin(authentication, #orgId)")
    @Operation(summary = "Create escrow transaction",
               description = "Create an escrow transaction for a product sale and hold funds",
               requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                   description = "Escrow creation payload",
                   required = true,
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = EscrowCreateRequestDTO.class),
                                      examples = {@ExampleObject(value = "{\"product_id\":451,\"amount\":12000.00,\"currency\":\"XOF\",\"metadata\":\"{\\\"orderId\\\":\\\"ORD-1234\\\"}\"}")})
               ),
               responses = {@ApiResponse(responseCode = "201", description = "Escrow created"), @ApiResponse(responseCode = "404", description = "Organisation not found")})
    public ResponseEntity<?> createEscrow(
            @PathVariable Long orgId,
            @RequestBody @Valid EscrowCreateRequestDTO request) {
        try {
            var escrow = organisationService.createEscrow(
                orgId,
                request.getProductId(),
                request.getAmount(),
                request.getMetadata(),
                request.getCurrency()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(escrow);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping("/{orgId}/escrow/{escrowId}/release")
    @PreAuthorize("@organisationSecurity.canModerateOrganisation(authentication, #orgId)")
    @Operation(summary = "Release escrow transaction",
               description = "Release held escrow funds to the seller/recipient after verification or when conditions are met",
               responses = {@ApiResponse(responseCode = "200", description = "Escrow released"), @ApiResponse(responseCode = "404", description = "Escrow not found")})
    public ResponseEntity<?> releaseEscrow(
            @PathVariable Long orgId,
            @PathVariable Long escrowId,
            @RequestAttribute("userId") Long moderatorId) {
        try {
            var escrow = organisationService.releaseEscrow(escrowId, moderatorId);
            return ResponseEntity.ok(escrow);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping("/{orgId}/escrow/{escrowId}/refund")
    @PreAuthorize("@organisationSecurity.canModerateOrganisation(authentication, #orgId)")
    @Operation(summary = "Refund escrow transaction",
               description = "Refund an escrow transaction after dispute or guarantee claim resolution",
               requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                   description = "Escrow refund payload",
                   required = true,
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = EscrowRefundRequestDTO.class),
                                      examples = {@ExampleObject(value = "{\"reason\":\"Customer returned the item.\"}")})
               ),
               responses = {@ApiResponse(responseCode = "200", description = "Refund processed"), @ApiResponse(responseCode = "404", description = "Escrow transaction not found")})
    public ResponseEntity<?> refundEscrow(
            @PathVariable Long orgId,
            @PathVariable Long escrowId,
            @RequestBody @Valid EscrowRefundRequestDTO request) {
        try {
            var escrow = organisationService.refundEscrow(escrowId, request.getReason());
            return ResponseEntity.ok(escrow);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping("/{orgId}/escrows")
    @PreAuthorize("@organisationSecurity.canAccessProducts(authentication, #orgId)")
    @Operation(summary = "List escrow transactions",
               description = "Retrieve escrow transactions for the organisation",
               responses = {@ApiResponse(responseCode = "200", description = "Escrow list returned",
                   content = @Content(mediaType = "application/json",
                       array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @Schema(implementation = EscrowTransactionDTO.class)),
                       examples = @ExampleObject(value = "[{\"id\":101,\"product_id\":451,\"amount\":12000.00,\"status\":\"HELD\",\"metadata\":\"{\\\"orderId\\\":\\\"ORD-1234\\\"}\",\"created_at\":\"2026-07-20T10:00:00Z\",\"released_at\":null}]")
                   )
               )})
    public ResponseEntity<List<EscrowTransactionDTO>> getOrganisationEscrows(@PathVariable Long orgId) {
        List<EscrowTransactionDTO> escrows = organisationService.getEscrowTransactionsByOrganisation(orgId).stream()
                .map(this::toEscrowTransactionDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(escrows);
    }

    private EscrowTransactionDTO toEscrowTransactionDTO(com.mo.core.model.organisations.EscrowTransaction escrow) {
        EscrowTransactionDTO dto = new EscrowTransactionDTO();
        dto.setId(escrow.getId());
        dto.setProductId(escrow.getProductId());
        dto.setAmount(escrow.getAmount());
        dto.setStatus(escrow.getStatus() != null ? escrow.getStatus().name() : null);
        dto.setMetadata(escrow.getMetadata());
        dto.setCreatedAt(escrow.getCreatedAt());
        dto.setReleasedAt(escrow.getReleasedAt());
        return dto;
    }

    @GetMapping("/{orgId}/moderation-queue")
    @PreAuthorize("@organisationSecurity.canModerateOrganisation(authentication, #orgId)")
    @Operation(summary = "Get moderation queue", description = "Return the current moderation queue for organisation content")
    public ResponseEntity<?> getModerationQueue(@PathVariable Long orgId) {
        var queue = organisationService.getModerationQueue();
        return ResponseEntity.ok(queue);
    }

    @GetMapping("/{orgId}/sla-exceeded")
    @PreAuthorize("@organisationSecurity.isAdminOfOrganisation(authentication, #orgId)")
    @Operation(summary = "Get SLA exceeded items", description = "Return organisation items that have exceeded SLA thresholds")
    public ResponseEntity<?> getSlaExceeded(@PathVariable Long orgId) {
        var items = organisationService.getSlaExceededItems();
        return ResponseEntity.ok(items);
    }

    // Webhook subscription management
    @PostMapping("/{orgId}/webhooks")
    @PreAuthorize("@organisationSecurity.isOwnerOrAdmin(authentication, #orgId)")
    @Operation(summary = "Create webhook subscription",
               description = "Create a webhook subscription for organisation events such as validation or escrow updates",
               requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                   description = "Webhook subscription payload",
                   required = true,
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = WebhookSubscriptionRequestDTO.class),
                                      examples = {@ExampleObject(value = "{\"url\":\"https://example.com/webhook\",\"event_types\":\"PENDING,RELEASE,REFUND\",\"secret\":\"super-secret\"}")})
               ),
               responses = {@ApiResponse(responseCode = "201", description = "Webhook created"), @ApiResponse(responseCode = "400", description = "Bad Request")})
    public ResponseEntity<?> createWebhookSubscription(
            @PathVariable Long orgId,
            @RequestBody @Valid WebhookSubscriptionRequestDTO request) {
        try {
            String url = request.getUrl();
            String eventTypes = request.getEventTypes();
            String secret = request.getSecret();
            com.mo.core.model.organisations.WebhookSubscription sub = webhookService.createSubscription(orgId, url, eventTypes, secret);
            return ResponseEntity.status(HttpStatus.CREATED).body(sub);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/{orgId}/webhooks")
    @PreAuthorize("@organisationSecurity.isOwnerOrAdmin(authentication, #orgId)")
    @Operation(summary = "List webhook subscriptions", description = "List webhook subscriptions configured for the organisation")
    public ResponseEntity<?> listWebhookSubscriptions(@PathVariable Long orgId) {
        var subs = webhookService.listSubscriptions(orgId);
        return ResponseEntity.ok(subs);
    }

    @DeleteMapping("/{orgId}/webhooks/{id}")
    @PreAuthorize("@organisationSecurity.isOwnerOrAdmin(authentication, #orgId)")
    @Operation(summary = "Delete webhook subscription", description = "Remove an organisation webhook subscription")
    public ResponseEntity<?> deleteWebhookSubscription(@PathVariable Long orgId, @PathVariable Long id) {
        try {
            webhookService.deactivateSubscription(orgId, id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
    
    @PreAuthorize("@organisationSecurity.isOwnerOrAdmin(authentication, #orgId)")
    @Transactional
    public ResponseEntity<?> createNeedsForOrganisation(
            @PathVariable Long orgId,
            @RequestBody @Valid BulkNeedsForOrganisationRequest request) {

        log.info("➡️ Creating {} needs for organisation {}", request.needs().size(), orgId);

        Organisation organisation = organisationService.getOrganisationById(orgId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organisation not found"));

        UserNeedVisitor<AbstractUserNeed> createVisitor =
                visitorRegistry.getVisitorForNeedType("createUserNeedVisitor");
        if (createVisitor == null) {
            throw new IllegalArgumentException("No visitor found for createUserNeedVisitor");
        }

        List<AbstractUserNeed> savedNeeds = new ArrayList<>();

        for (AbstractUserNeedDto dto : request.needs()) {
            if (dto == null) {
                log.warn("Invalid need payload, skipping.");
                continue;
            }

            AbstractUserNeed savedNeed = userNeedService.createFromDto(dto);
            savedNeeds.add(savedNeed);

            // bidirectional link
            organisation.addNeed(savedNeed);
        }

        organisationService.save(organisation); // propriétaire => on persiste l’organisation

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("organisationId", orgId);
        resp.put("savedNeedIds", savedNeeds.stream().map(AbstractUserNeed::getId).toList());
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping("/{orgId}/needs")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get organisation needs",
               description = "Retrieve paginated list of needs for the specified organisation",
               responses = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<?> getOrganisationNeeds(
            @PathVariable Long orgId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {

        try {
            Organisation organisation = organisationService.getOrganisationById(orgId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organisation not found"));
            
            List<AbstractUserNeed> allNeeds = new ArrayList<>(organisation.getNeeds());
            
            // Manual pagination
            int start = page * size;
            int end = Math.min(start + size, allNeeds.size());
            List<AbstractUserNeed> pageNeeds = allNeeds.subList(start, end);
            
            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("organisationId", orgId);
            resp.put("page", page);
            resp.put("size", size);
            resp.put("totalElements", allNeeds.size());
            resp.put("totalPages", (int) Math.ceil((double) allNeeds.size() / size));
            resp.put("content", pageNeeds.stream().map(needmapper::mapToDto).collect(Collectors.toList()));
            
            return ResponseEntity.ok(resp);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PatchMapping("/{orgId}/products/{productId}/certify")
    @PreAuthorize("@organisationSecurity.isOwnerOrAdmin(authentication, #orgId)")
    @Operation(summary = "Certify a product for the organisation",
               description = "Owner or admin can certify or uncertify a product attached to the organisation",
               requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                   description = "Certification payload with a boolean 'certified' field",
                   required = true,
                   content = @Content(mediaType = "application/json",
                       examples = @ExampleObject(value = "{\"certified\": true}")))
    )
    public ResponseEntity<?> certifyProduct(
            @PathVariable Long orgId,
            @PathVariable Long productId,
            @RequestBody Map<String, Boolean> request) {

        try {
            Boolean certified = request.get("certified");
            if (certified == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Field 'certified' is required"));
            }

            AbstractProduct product = organisationService.certifyProduct(orgId, productId, certified);
            return ResponseEntity.ok(product);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }
}
