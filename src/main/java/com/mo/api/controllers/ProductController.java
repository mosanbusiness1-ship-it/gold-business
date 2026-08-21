package com.mo.api.controllers;

import java.io.ByteArrayInputStream;
import com.mo.core.events.StrictMatchEvent;
import com.mo.core.events.FilteredSimilarMatchEvent;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.mo.core.dtos.ProductAndMatchedNeedsDTO;
import com.mo.core.dtos.productsDtos.AbstractProductDto;
import com.mo.core.dtos.productsDtos.CreateProductResponseDto;
import com.mo.core.enums.ProductType;
import com.mo.core.kafka.producers.MessageProducer;
import com.mo.core.model.products.AbstractProduct;
import com.mo.core.model.products.ProductHistory;
import com.mo.core.services.AuditService;
import com.mo.core.services.ElasticsearchService;
import com.mo.core.services.ProductService;
import com.mo.core.visitors.product_visitors.ProductVisitor;
import com.mo.mappers.productsMappers.ProductMapperJackson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/public/products")

public class ProductController {
    // private String user_role = "ADMIN"; // Exemple de rôle, à adapter selon votre
    // logique
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    private final ProductService service;
    private final AuditService auditService;
    private final ElasticsearchService elasticsearchService;
    private final MessageProducer messageProducer;
    private final ProductMapperJackson mapperVisitor;
    
	@Autowired
	private ApplicationEventPublisher eventPublisher;
	
    
    public ProductController(ProductService service, 
            AuditService auditService,
            ElasticsearchService elasticsearchService, 
            ProductMapperJackson mapperVisitor,
            MessageProducer kafkaProducer) {
        this.service = service;
        this.auditService = auditService;
        this.elasticsearchService = elasticsearchService;
        this.messageProducer = kafkaProducer;
        this.mapperVisitor = mapperVisitor;
    }

    // Create product using service orchestration (factory -> visitor -> repository)
    // @PreAuthorize("hasRole('ROLE_SENDER') or hasRole('ROLE_ADMIN')")
@CrossOrigin(origins = "http://localhost:9000")
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
@Operation(
    summary = "Create a product",
    description = "Create a product and return the created product together with matching needs",
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        content = @Content(
            mediaType = "application/json",
            examples = {
                @ExampleObject(
                    name = "XAF product example",
                    value = "{\"name\": \"iPhone 11 Pro\", \"description\": \"iphone 11 pro desc\", \"price\": 150000, \"quantity\": 10, \"currency\": \"XAF\", \"type\": \"ELECTRONIC\", \"enabled\": true, \"version\": 1, \"owner_id\": \"1\", \"photo_urls\": [\"https://example.com/photos/iphone11.jpg\"], \"created_at\": \"2026-07-20T19:06:08.934Z\", \"updated_at\": \"2026-07-20T19:06:08.934Z\", \"is_platform_owner\": false, \"brand\": \"Apple\", \"model\": \"iPhone 11 Pro\", \"specifications\": \"{ \\\"ram\\\": \\\"4GB\\\", \\\"storage\\\": \\\"64GB\\\" }\", \"electronic_type\": \"PHONE\", \"warranty_period\": \"12\"}"
                ),
                @ExampleObject(
                    name = "EURO product example",
                    value = "{\"name\": \"Veste premium\", \"description\": \"Veste en cuir\", \"price\": 1200, \"quantity\": 20, \"currency\": \"EURO\", \"type\": \"FASHION\", \"enabled\": true, \"version\": 1, \"owner_id\": \"1\", \"photo_urls\": [\"https://example.com/photos/jacket.jpg\"], \"created_at\": \"2026-07-20T19:06:08.934Z\", \"updated_at\": \"2026-07-20T19:06:08.934Z\", \"is_platform_owner\": false, \"fashion_type\": \"CLOTHING\", \"size\": \"M\", \"size_system\": \"EU\", \"color\": \"Noir\", \"material\": \"Cuir\", \"brand\": \"MarqueX\", \"target_gender\": \"UNISEX\"}"
                ),
                @ExampleObject(
                    name = "USD service product example",
                    value = "{\"name\": \"Séance de coaching 1h\", \"description\": \"Coaching individuel\", \"price\": 5000, \"quantity\": 10, \"currency\": \"USD\", \"type\": \"SERVICE\", \"enabled\": true, \"version\": 1, \"owner_id\": \"1\", \"photo_urls\": [], \"created_at\": \"2026-07-20T19:06:08.934Z\", \"updated_at\": \"2026-07-20T19:06:08.934Z\", \"is_platform_owner\": false, \"available_slots\": [\"2026-07-25T09:00:00.000Z\"], \"service_provider\": \"CoachPro\", \"location\": \"Paris\", \"duration\": 3600, \"online_available\": true, \"available_after\": \"2026-07-21T09:00:00.000Z\"}"
                )
            }
        )
    ),
    responses = {@ApiResponse(responseCode = "201", description = "Product created successfully", content = @Content(schema = @Schema(implementation = CreateProductResponseDto.class)))}
)
public ResponseEntity<CreateProductResponseDto> createProduct(@RequestBody AbstractProductDto productDto) {
    log.info("➡️ Received request to create product: {}", productDto);

    AbstractProduct savedProduct = service.createFromDto(productDto);
    log.info("✅ Product created: id={}, type={}, data={}", savedProduct.getId(), savedProduct.getType(), savedProduct);

    Map<String, Object> matchResults = service.searchMatchingNeeds(savedProduct);
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> strictMatches = (List<Map<String, Object>>) matchResults.getOrDefault("strictlyMatchingNeeds", List.of());
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> filteredSimilarMatches = (List<Map<String, Object>>) matchResults.getOrDefault("similarNeeds", List.of());

    CreateProductResponseDto responseDto = new CreateProductResponseDto();
    responseDto.setSavedProduct(mapperVisitor.mapToDtoObject(savedProduct));
    responseDto.setStrictlyMatchingNeeds(strictMatches);
    responseDto.setSimilarNeeds(filteredSimilarMatches);

    ProductAndMatchedNeedsDTO productAndStrictMatches = new ProductAndMatchedNeedsDTO(savedProduct, strictMatches);
    ProductAndMatchedNeedsDTO productAndSimilarMatches = new ProductAndMatchedNeedsDTO(savedProduct, filteredSimilarMatches);

    if (productAndStrictMatches != null) {
        eventPublisher.publishEvent(new StrictMatchEvent(this, productAndStrictMatches));
    }

    if (productAndSimilarMatches != null) {
		eventPublisher.publishEvent(new FilteredSimilarMatchEvent(this, productAndSimilarMatches));
	}
		
    log.info("✅ Réponse prête à être envoyée avec {} stricts et {} similaires", strictMatches.size(), filteredSimilarMatches.size());
    return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);


}


    

    // Lecture par ID
    @GetMapping("/{id}")
    @Operation(summary = "Get product by id", description = "Return a single product by its ID using DTO mapping")
    public JsonNode getProduct(@PathVariable Long id) {
        AbstractProduct product = service.getProductById(id);
        return mapperVisitor.mapToDto(product);
    }

    // Update a product using a DTO-based request
    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Update an existing product using a DTO payload")
    // @PreAuthorize("hasRole('ADMIN')")
    public AbstractProduct updateProduct(@PathVariable Long id, @RequestBody @Valid AbstractProductDto productDto) {
        return service.updateFromDto(id, productDto);
    }

    // Suppression
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Delete a product by ID")
    // @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        service.deleteProduct(id); // Suppression en base
        eventPublisher.publishEvent(id);
    }

    // ✅ Liste paginée
    @GetMapping
    @Operation(summary = "List products", description = "Return a pageable list of all products in DTO form")
    public Page<JsonNode> listProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {

        Page<AbstractProduct> products = service.findAll(PageRequest.of(page, size, Sort.by(sortBy).descending()));
        
        List<JsonNode> productDtos = products
            .stream()
            .map(mapperVisitor::mapToDto)
            .toList();

        return new PageImpl<>(productDtos, products.getPageable(), products.getTotalElements());
    }
    
    

    // ✅ Recherche simple par nom et type
    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Search products by name and type")
    public List<AbstractProduct> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ProductType type) {
        return service.searchByNameAndType(name, type);
    }

    // Recherche avec filtres (nom, type, prix min, prix max)
    @GetMapping("/search-with-filters")
    @Operation(summary = "Search products with filters", description = "Search products with optional price and type filters")
    public List<AbstractProduct> searchWithFilters(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ProductType type,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        return service.searchWithFilters(name, type, minPrice, maxPrice);
    }

    // ✅ Export CSV
    @GetMapping("/export")
    @Operation(summary = "Export products", description = "Export products data to CSV")
    public ResponseEntity<Resource> exportToCsv() {
        String csv = service.exportProductsToCsv();
        InputStreamResource resource = new InputStreamResource(
                new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

    // ✅ Activer / désactiver un produit (soft delete)
    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Toggle product status", description = "Enable or disable a product")
    public AbstractProduct toggleProduct(@PathVariable Long id) {
        return service.toggleProductStatus(id);
    }

    // ✅ Historique réel via ProductHistory
    @GetMapping("/{id}/audit")
    @Operation(summary = "Get product audit history", description = "Return audit history entries for a product")
    public List<ProductHistory> getProductAuditHistory(@PathVariable Long id) {
        return auditService.getProductHistory(id);
    }

    @GetMapping("/search-elasticsearch")
    @Operation(summary = "Search products in Elasticsearch", description = "Search products by keywords using Elasticsearch")
    public ResponseEntity<List<Map<String, Object>>> searchProductsElasticsearch(
            @RequestParam String keywords) {
        List<Map<String, Object>> results = elasticsearchService.searchProductsByKeywords(keywords);
        return ResponseEntity.ok(results);
    }
}
