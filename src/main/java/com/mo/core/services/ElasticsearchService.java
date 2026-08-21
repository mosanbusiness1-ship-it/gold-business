package com.mo.core.services;

import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Collections;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchHit;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mo.core.documents.products.AbstractProductDocument;
import com.mo.core.model.products.AbstractProduct;
import com.mo.core.visitors.product_visitors.ProductIndexerVisitor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ElasticsearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final ProductIndexerVisitor indexerVisitor;
    private final ObjectMapper objectMapper;
    private static final IndexCoordinates INDEX = IndexCoordinates.of("products");

    public ElasticsearchService(ElasticsearchOperations elasticsearchOperations, 
                                 ProductIndexerVisitor indexerVisitor,
                                 ObjectMapper objectMapper) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.indexerVisitor = indexerVisitor;
        this.objectMapper = objectMapper;
    }

    /**
     * Indexe un produit abstrait avec champs textuels concaténés dynamiquement.
     */
    public void indexProduct(AbstractProduct product) {
        try {
            AbstractProductDocument documentObject = product.accept(indexerVisitor);

            if (documentObject == null || product.getId() == null) {
                log.warn("Produit ou ID invalide, indexation ignorée.");
                return;
            }

            // 💡 Convertir le document en map pour la construction Elasticsearch
            Map<String, Object> productData = objectMapper.convertValue(documentObject, new TypeReference<>() {});

            // 🔧 Construire allText dynamiquement à partir des champs non nuls
            String allText = productData.entrySet().stream()
                    .filter(e -> e.getValue() != null)
                    .map(e -> {
                        if (e.getValue() instanceof Boolean boolVal) {
                            return e.getKey() + "_" + (boolVal ? "yes" : "no");
                        } else {
                            return e.getValue().toString();
                        }
                    })
                    .collect(Collectors.joining(" "));

            productData.put("allText", allText.toLowerCase().trim());

            // 🔥 Nom de l'index basé sur la classe
            String className = product.getClass().getSimpleName(); // e.g., VehicleProduct
            String indexName = className.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase() + "s"; // e.g., vehicle_products
            IndexCoordinates dynamicIndex = IndexCoordinates.of(indexName);

            // 🔄 Mise à jour ou insertion dans l'index
            Document esDocument = Document.from(productData);
            UpdateQuery query = UpdateQuery.builder(product.getId().toString())
                    .withDocument(esDocument)
                    .withDocAsUpsert(true)
                    .build();

            elasticsearchOperations.update(query, dynamicIndex);
            log.info("✅ Produit indexé dans l'index '{}': id={}, type={}", indexName, product.getId(), className);

        } catch (Exception e) {
            log.error("❌ Erreur d'indexation du produit : {}", product.getId(), e);
        }
    }

    public List<Map<String, Object>> searchProductsByKeywords(String keywords) {
        try {
            Criteria criteria = new Criteria("allText").matches(keywords);
            Query query = new CriteriaQuery(criteria);
            query.setPageable(PageRequest.of(0, 20)); // pagination

            return elasticsearchOperations
                    .search(query, Map.class, INDEX)
                    .getSearchHits()
                    .stream()
                    .map(SearchHit::getContent)
                    .map(c -> (Map<String, Object>) c)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("❌ Erreur lors de la recherche : {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public void deleteProductFromIndex(Long productId) {
        try {
            String deletedId = elasticsearchOperations.delete(productId.toString(), INDEX);
            log.info("🗑️ Produit supprimé de l'index : id={}", deletedId);
        } catch (Exception e) {
            log.error("❌ Erreur lors de la suppression du produit : {}", productId, e);
        }
    }
}
