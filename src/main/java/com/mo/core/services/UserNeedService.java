package com.mo.core.services;

import com.mo.core.model.needs.ElectronicNeed;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import com.mo.core.model.needs.FashionNeed;
import com.mo.core.model.needs.FoodNeed;
import com.mo.core.model.needs.RealEstateNeed;
import com.mo.core.model.needs.ServiceNeed;
import com.mo.core.model.needs.AbstractUserNeed;
import com.mo.core.documents.needs.AbstractUserNeedDocument;
import com.mo.core.documents.products.AbstractProductDocument;
import com.mo.core.enums.NeedType;
import com.mo.core.enums.ProductType;
import com.mo.core.model.needs.VehicleNeed;
import com.mo.core.model.products.AbstractProduct;
import com.mo.core.model.products.ElectronicProduct;
import com.mo.core.model.products.FashionProduct;
import com.mo.core.model.products.FoodProduct;
import com.mo.core.model.products.RealEstateProduct;
import com.mo.core.model.products.ServiceProduct;
import com.mo.core.model.products.VehicleProduct;
import com.mo.core.repositories.jpa.UserNeedRepository;
import com.mo.core.visitors.need_visitors.UserNeedIndexerVisitor;
import com.mo.core.visitors.need_visitors.UserNeedVisitor;
import com.mo.core.visitors.need_visitors.UserNeedVisitorRegistry;
import com.mo.core.visitors.product_visitors.ProductIndexerVisitor;
import com.mo.core.factories.UserNeedFactory;
import com.mo.core.dtos.userNeedsDTO.AbstractUserNeedDto;
import org.springframework.beans.factory.annotation.Qualifier;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mo.core.kafka.producers.MessageProducer;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.elasticsearch.client.elc.NativeQuery;

@Slf4j
@Service
public class UserNeedService {

    private final UserNeedIndexerVisitor indexerVisitor;
    private final UserNeedRepository userNeedRepository;
    private final ProductNeedMatcher matcher;
    private final ElasticsearchOperations elasticsearchOperations;
    private final UserNeedVisitorRegistry visitorRegistry;
    private final UserNeedFactory userNeedFactory;
    private final ObjectMapper objectMapper;
    private final MessageProducer messageProducer;
    private final UserNeedVisitor<AbstractUserNeed> createUserNeedVisitor;

    public UserNeedService(
            UserNeedRepository userNeedRepository,
            ProductNeedMatcher matcher,
            UserNeedIndexerVisitor indexerVisitor,
            ElasticsearchOperations elasticsearchOperations,
            UserNeedVisitorRegistry visitorRegistry,
            UserNeedFactory userNeedFactory,
            ObjectMapper objectMapper,
            MessageProducer messageProducer,
            @Qualifier("createUserNeedVisitor") UserNeedVisitor<AbstractUserNeed> createUserNeedVisitor) {
        this.userNeedRepository = userNeedRepository;
        this.matcher = matcher;
        this.indexerVisitor = indexerVisitor;
        this.elasticsearchOperations = elasticsearchOperations;
        this.visitorRegistry = visitorRegistry;
        this.userNeedFactory = userNeedFactory;
        this.objectMapper = objectMapper;
        this.messageProducer = messageProducer;
        this.createUserNeedVisitor = createUserNeedVisitor;
    }

    public AbstractUserNeed createNeed(AbstractUserNeed need) {
        AbstractUserNeed saved = userNeedRepository.save(need);
        indexNeed(saved);
        return saved;
    }

    public AbstractUserNeed createFromDto(AbstractUserNeedDto dto) {
        if (dto == null) throw new IllegalArgumentException("UserNeed DTO must not be null");
        AbstractUserNeed need = userNeedFactory.create(dto);
        AbstractUserNeed saved = need.accept(createUserNeedVisitor);
        indexNeed(saved);
        return saved;
    }

    public List<AbstractUserNeed> getNeedsByUserId(Long userId) {
        return userNeedRepository.findByUserId(userId);
    }

    // recuperation sql
    public List<AbstractProduct> findMatchingProductsForUser(Long userId) {
        List<AbstractUserNeed> needs = userNeedRepository.findByUserId(userId);

        return needs.stream()
            .map(matcher::matchProducts) // retourne List<AbstractProduct>
            .flatMap(List::stream)       // transforme en Stream<AbstractProduct>
            .distinct()
            .toList();                   // Java 16+
    }
    
    
    public List<AbstractUserNeed> getAllNeeds() {
        return userNeedRepository.findAll();
    }

    public void deleteNeed(Long id) {
        if (!userNeedRepository.existsById(id)) {
            throw new EntityNotFoundException("UserNeed with id " + id + " not found");
        }

        // Suppression dans la base relationnelle
        userNeedRepository.deleteById(id);

        // Suppression dans Elasticsearch
        elasticsearchOperations.delete(id.toString(), AbstractUserNeed.class);
    }



    public void indexNeed(AbstractUserNeed need) {
        try {
            AbstractUserNeedDocument document = need.accept(indexerVisitor);
            if (document == null || need.getId() == null) {
                log.warn("Besoin ou ID invalide, indexation ignorée.");
                return;
            }

            IndexCoordinates index = IndexCoordinates.of(buildIndexName(document.getClass()));
                // Save the need document to Elasticsearch with business metadata and also emit an async event.
            elasticsearchOperations.save(document, index);
            log.info("✅ Besoin indexé dans '{}': id={}", index.getIndexName(), need.getId());
            publishNeedIndexingEvent(document);

        } catch (Exception e) {
            log.error("❌ Erreur d'indexation du besoin : {}", need.getId(), e);
        }
    }

    private void publishNeedIndexingEvent(AbstractUserNeedDocument document) {
        // Publish an async event so indexing and analytics consumers can react in a decoupled way.
        try {
            String payload = objectMapper.writeValueAsString(document);
            messageProducer.send("need-indexing", payload);
            log.info("📤 Published need indexing event for id={}", document.getId());
        } catch (Exception e) {
            log.error("❌ Failed to publish need indexing event for id={}: {}", document.getId(), e.getMessage(), e);
        }
    }
    
   
    //construit le nom de l'index du need
    private String buildIndexName(Class<?> clazz) {
        String simpleName = clazz.getSimpleName(); 
        String snakeCase = simpleName
            .replaceAll("([a-z])([A-Z])", "$1_$2")  
            .replaceAll("_need$", "")           
            .toLowerCase();
        return snakeCase + "s"; 
    }


    // Determine le nom de l'index du produit elastic search pour le type de besoin
	public String determineIndexNameForNeedType(NeedType type) {
	    return switch (type) {
	        case VEHICLE -> "vehicle_product_documents";
	        case ELECTRONIC -> "electronic_product_documents";
	        case FASHION -> "fashion_product_documents";
	        case FOOD -> "food_product_documents";
	        case REALESTATE -> "real_estate_product_documents";
	        case SERVICE -> "service_product_documents";
	        default -> throw new IllegalArgumentException("Unknown pro type: " + type);
	    };
	}

	//Recherche des produits matchés par le besoin enregistré
	public List<Map<String, Object>> searchMatchingProducts(AbstractUserNeed need) {
	 UserNeedVisitor<Criteria> matchingVisitor = visitorRegistry.getVisitorTyped("matchingVisitorForNeeds");
	 if (matchingVisitor == null) {
		 log.error("❌ Aucun visitor trouvé pour 'umatchingVisitorForNeeds'");
	     throw new IllegalArgumentException("No visitor found for matchingVisitorForNeeds");
	 }
	
	 Criteria criteria = need.accept(matchingVisitor);
	 log.info("🔎 Critères générés pour la recherche de produits : {}", criteria);
	 
	 CriteriaQuery searchQuery = new CriteriaQuery(criteria);
	 // Pagination par défaut et réduction du _source pour minimiser le payload
	 searchQuery.setPageable(org.springframework.data.domain.PageRequest.of(0, 20));
	 String productIndex = determineIndexNameForNeedType(need.getType());
	 try {
	     return elasticsearchOperations.search(searchQuery, Map.class, IndexCoordinates.of(productIndex))
	             .getSearchHits()
	             .stream()
	             .map(hit -> (Map<String, Object>) hit.getContent())
	             .toList();
	 } catch (Exception e) {
	     log.error("Erreur ES: {}", e.getMessage(), e);
	     return List.of();
	 }
	 
}


}

