package com.mo.core.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mo.auth.User;
import com.mo.core.documents.products.AbstractProductDocument;
import com.mo.core.enums.ProductType;
import com.mo.core.kafka.producers.MessageProducer;
import com.mo.core.model.products.AbstractProduct;
import com.mo.core.model.products.ElectronicProduct;
import com.mo.core.model.products.FashionProduct;
import com.mo.core.model.products.FoodProduct;
import com.mo.core.model.products.ProductHistory;
import com.mo.core.model.products.RealEstateProduct;
import com.mo.core.model.products.ServiceProduct;
import com.mo.core.model.products.VehicleProduct;
import com.mo.core.model.specifications.ProductSpecification;
import com.mo.core.repositories.jpa.ProductRepository;
import com.mo.core.matchers.NeedProductMatcher;
import com.mo.core.visitors.product_visitors.ProductIndexerVisitor;
import com.mo.core.visitors.product_visitors.ProductVisitor;
import com.mo.core.visitors.product_visitors.ProductVisitorRegistry;
import com.mo.core.factories.ProductFactory;
import com.mo.core.dtos.productsDtos.AbstractProductDto;
import org.springframework.beans.factory.annotation.Qualifier;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class ProductService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final ProductIndexerVisitor indexerVisitor;
    private final ProductVisitorRegistry visitorRegistry;
    private final NeedProductMatcher needProductMatcher;
    
    private final MessageProducer messageProducer;
    private final ObjectMapper objectMapper;
    private final ProductRepository repository;
    private final AuditService auditService;
    //private final KafkaTemplate<String, String> kafkaTemplate;
    private final ProductVisitor<AbstractProductDocument> productIndexerVisitor;

    private final Map<Long, List<String>> updateHistory = new ConcurrentHashMap<>();
    private final ProductFactory productFactory;
    private final ProductVisitor<AbstractProduct> createProductVisitor;

    public ProductService(
            ProductRepository repository,
            AuditService auditService,
            ProductVisitor<AbstractProductDocument> productIndexerVisitor,
            ObjectMapper objectMapper,
            ElasticsearchOperations elasticsearchOperations,
            MessageProducer messageProducer,
            ProductIndexerVisitor indexerVisitor,
            ProductVisitorRegistry visitorRegistry,
            NeedProductMatcher needProductMatcher,
            ProductFactory productFactory,
            @Qualifier("createProductVisitor") ProductVisitor<AbstractProduct> createProductVisitor) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.indexerVisitor = indexerVisitor;
        this.repository = repository;
        this.messageProducer = messageProducer;
        this.auditService = auditService;
        this.productIndexerVisitor = productIndexerVisitor;
        this.objectMapper = objectMapper;
        this.visitorRegistry = visitorRegistry;
        this.needProductMatcher = needProductMatcher;
        this.productFactory = productFactory;
        this.createProductVisitor = createProductVisitor;
    }

    public AbstractProduct createFromDto(AbstractProductDto dto) {
        AbstractProduct product = productFactory.create(dto);
        AbstractProduct saved = product.accept(createProductVisitor);
        indexProduct(saved);
        return saved;
    }

    public Map<String, Object> searchMatchingNeeds(AbstractProduct savedProduct) {
        ProductVisitor<NativeQuery> matchingVisitor = visitorRegistry.getVisitorTyped("matchingVisitor");
        if (matchingVisitor == null) {
            log.error("❌ No visitor found for matchingVisitor");
            throw new IllegalArgumentException("No visitor found for matchingVisitor");
        }

        NativeQuery nativeQuery = savedProduct.accept(matchingVisitor);
        log.info("🔎 Generated NativeQuery for similar needs search: {}", nativeQuery);

        String needIndex = determineIndexNameForNeedType(savedProduct.getType());
        log.info("🔍 Searching need index: {}", needIndex);

        List<SearchHit<Map>> matchingHits;
        try {
            matchingHits = elasticsearchOperations.search(nativeQuery, Map.class, IndexCoordinates.of(needIndex))
                    .getSearchHits();
            log.info("🔁 Found {} matching need documents", matchingHits.size());
        } catch (Exception e) {
            log.error("❌ Elasticsearch matching failure: {}", e.getMessage(), e);
            matchingHits = Collections.emptyList();
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> allCandidateNeeds = matchingHits.stream()
                .map(hit -> (Map<String, Object>) hit.getContent())
                .toList();

        List<Map<String, Object>> strictMatches = allCandidateNeeds.stream()
                .filter(need -> needProductMatcher.productMatchesNeed(savedProduct, need))
                .toList();

        List<Map<String, Object>> similarMatches = allCandidateNeeds.stream()
                .filter(need -> {
                    Object notifyObj = need.get("notify_similar_products");
                    if (!(notifyObj instanceof Boolean notify) || !notify) return false;
                    if (!needProductMatcher.productSatisfiesMandatoryFields(savedProduct, need)) return false;
                    return needProductMatcher.productSatisfiesImportantFields(savedProduct, need);
                })
                .toList();

        List<Map<String, Object>> filteredSimilarMatches = similarMatches.stream()
                .filter(similar -> strictMatches.stream()
                        .noneMatch(strict -> strict.toString().equals(similar.toString())))
                .toList();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("strictlyMatchingNeeds", strictMatches);
        result.put("similarNeeds", filteredSimilarMatches);
        return result;
    }

    public AbstractProduct updateFromDto(Long id, AbstractProductDto dto) {
        AbstractProduct existingProduct = getProductById(id);
        AbstractProduct updatedProduct = productFactory.create(dto);
        updatedProduct.setId(id);
        updatedProduct.setOwner(existingProduct.getOwner());
        return saveProduct(updatedProduct);
    }

    public <T extends AbstractProduct> T saveProduct(T product) {
        T saved = repository.save(product);
        logHistory(saved.getId(), "Produit sauvegardé ou mis à jour.");

        // Envoie des données à Kafka pour indexation
        // Save the product in SQL and update Elasticsearch, then publish an async indexing event.
        AbstractProductDocument indexData = saved.accept(productIndexerVisitor);
        indexProduct(saved);
        publishProductIndexingEvent(indexData);

        return saved;
    }



    public AbstractProduct getProductById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produit introuvable avec l'ID : " + id));
    }

    public Page<AbstractProduct> findAll(Pageable pageable) {
        return repository.findAll((root, query, cb) -> cb.isTrue(root.get("enabled")), pageable);
    }

    public List<AbstractProduct> searchByNameAndType(String name, ProductType type) {
        return repository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null && !name.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }

    public List<AbstractProduct> search(ProductSpecification spec) {
        return repository.findAll(spec.toSpecification());
    }

    public AbstractProduct toggleProductStatus(Long id) {
        AbstractProduct product = getProductById(id);
        product.setEnabled(!product.isEnabled());
        logHistory(id, "Statut modifié : " + (product.isEnabled() ? "activé" : "désactivé"));
        return repository.save(product);
    }

    public List<AbstractProduct> searchWithFilters(String name, ProductType type, BigDecimal minPrice,
            BigDecimal maxPrice) {
        return repository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null && !name.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }

    private void logHistory(Long productId, String action) {
        updateHistory.computeIfAbsent(productId, k -> new ArrayList<>()).add(LocalDateTime.now() + " - " + action);
    }

    private String escapeCsv(String value) {
        if (value == null)
            return "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    public String exportProductsToCsv() {
        List<AbstractProduct> products = repository.findAll();

        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("ID,Nom,Description,Prix,Type,Statut,Version\n");

        for (AbstractProduct product : products) {
            csvBuilder.append(product.getId()).append(",");
            csvBuilder.append(escapeCsv(product.getName())).append(",");
            csvBuilder.append(escapeCsv(product.getDescription())).append(",");
            csvBuilder.append(product.getPrice()).append(",");
            csvBuilder.append(product.getType()).append(",");
            csvBuilder.append(product.isEnabled() ? "Actif" : "Inactif").append(",");
            csvBuilder.append(product.getVersion()).append("\n");
        }

        return csvBuilder.toString();
    }

    public AbstractProduct updateProduct(Long id, AbstractProduct updatedProduct) {
        AbstractProduct existingProduct = getProductById(id);
        int oldVersion = existingProduct.getVersion();

        // Mettre à jour les champs du produit
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setType(updatedProduct.getType());
        existingProduct.setEnabled(updatedProduct.isEnabled());

        // Incrémenter la version
        existingProduct.setVersion(oldVersion + 1);

        // Sauvegarder le produit
        AbstractProduct savedProduct = saveProduct(existingProduct);

        // Ajouter l'historique pour cette mise à jour
        auditService.logHistory(id, "Produit mis à jour", oldVersion, savedProduct.getVersion());

        return savedProduct;
    }

    public List<AbstractProduct> getBoutiqueProducts(User appOwner) {
        return repository.findAll((root, query, cb) -> cb.and(
                cb.equal(root.get("owner"), appOwner),
                cb.isTrue(root.get("enabled"))));
    }

    public List<AbstractProduct> getMarketplaceProducts(User appOwner) {
        return repository.findAll((root, query, cb) -> cb.and(
                cb.notEqual(root.get("owner"), appOwner),
                cb.isTrue(root.get("enabled"))));
    }

    public void deleteProduct(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Produit introuvable avec l'ID : " + id);
        }
        repository.deleteById(id);
        auditService.logHistory(id, "Produit supprimé", -1, -1); // Aucun changement de version pour suppression
    }

    public List<ProductHistory> getProductHistory(Long id) {
        return auditService.getProductHistory(id);
    }

    public AbstractProduct setCertificationStatus(Long productId, boolean certified) {
        AbstractProduct product = getProductById(productId);
        product.setCertified(certified);
        return saveProduct(product);
    }











    public void indexProduct(AbstractProduct product) {
        try {
            AbstractProductDocument document = product.accept(indexerVisitor);
            if (document == null || product.getId() == null) {
                log.warn("Produit ou ID invalide, indexation ignorée.");
                return;
            }

            IndexCoordinates index = IndexCoordinates.of(buildIndexName(document.getClass()));
            elasticsearchOperations.save(document, index);
            log.info("✅ Produit indexé dans '{}': id={}", index.getIndexName(), product.getId());

        } catch (Exception e) {
            log.error("❌ Erreur d'indexation du produit : {}", product.getId(), e);
        }
    }

    private void publishProductIndexingEvent(AbstractProductDocument document) {
        // Publish an async event so downstream consumers can react without blocking the API request.
        try {
            String payload = objectMapper.writeValueAsString(document);
            messageProducer.send("product-indexing", payload);
            log.info("📤 Published product indexing event for id={}", document.getId());
        } catch (Exception e) {
            log.error("❌ Failed to publish product indexing event for id={}: {}", document.getId(), e.getMessage(), e);
        }
    }

    private String buildIndexName(Class<?> clazz) {
    String simpleName = clazz.getSimpleName(); // e.g., VehicleProduct
    String snakeCase = simpleName
        .replaceAll("([a-z])([A-Z])", "$1_$2")  // camelCase → snake_case
        .replaceAll("_product$", "")           // supprime le suffixe "_product"
        .toLowerCase();
    return snakeCase + "s"; // ex: vehicle → vehicles
}


    public String determineIndexNameForNeedType(ProductType type) {
	    return switch (type) {
	        case VEHICLE -> "vehicle_need_documents";
	        case ELECTRONIC -> "electronic_need_documents";
	        case FASHION -> "fashion_need_documents";
	        case FOOD -> "food_need_documents";
	        case REALESTATE -> "real_need_documents";
	        case SERVICE -> "service_need_documents";
	        default -> throw new IllegalArgumentException("Unknown product type: " + type);
	    };
    }
}

