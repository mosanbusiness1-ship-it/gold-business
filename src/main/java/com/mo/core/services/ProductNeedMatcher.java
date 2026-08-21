package com.mo.core.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import com.mo.core.documents.products.AbstractProductDocument;
import com.mo.core.model.needs.*;
import com.mo.core.model.products.*;
import com.mo.core.visitors.need_visitors.UserNeedMatchingVisitor;
import com.mo.core.visitors.product_visitors.ProductIndexerVisitor;
import com.mo.core.visitors.product_visitors.ProductVisitor;
import com.mo.core.visitors.product_visitors.ProductVisitorRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProductNeedMatcher {

    private static final Logger log = LoggerFactory.getLogger(ProductNeedMatcher.class);

    private final ProductVisitorRegistry visitorRegistry;
    private final ElasticsearchOperations elasticsearchOperations;
    private final UserNeedMatchingVisitor userNeedMatchingVisitor;

    @Autowired
    ProductIndexerVisitor productIndexerVisitor; // Assurez-vous que ce visitor est correctement initialisé

    public ProductNeedMatcher(ElasticsearchOperations elasticsearchOperations,
            UserNeedMatchingVisitor userNeedMatchingVisitor,
            ProductVisitorRegistry visitorRegistry) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.userNeedMatchingVisitor = userNeedMatchingVisitor;
        this.visitorRegistry = visitorRegistry;
    }

    public List<AbstractProduct> matchProducts(AbstractUserNeed need) {
        // Construire la requête CriteriaQuery via visitor (doit retourner Criteria)
        Criteria criteria = need.accept(userNeedMatchingVisitor);

        CriteriaQuery query = new CriteriaQuery(criteria);

        SearchHits<AbstractProduct> searchHits = elasticsearchOperations.search(query, AbstractProduct.class);

        return searchHits.getSearchHits().stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
    }

    /**
     * Cherche les besoins utilisateurs (UserNeed) qui correspondent à un produit
     * donné.
     * 
     * @param product produit publié
     * @return liste de besoins correspondants
     */
    public List<AbstractUserNeed> matchNeeds(AbstractProduct product) {
        // 1. Log structure de données à indexer
    	AbstractProductDocument productData = product.accept(productIndexerVisitor); // 👈 utilise ton visitor existant
        System.out.println("🧩 Données à indexer :");
        //productData.forEach((key, value) -> System.out.println("   ➤ " + key + " : " + value));

        // 2. Construction des critères pour matcher les besoins
        ProductVisitor<Criteria> visitor = visitorRegistry.getVisitorTyped("matchingVisitor");
        Criteria criteria = product.accept(visitor);

        System.out.println("🔍 Critères Elasticsearch générés : " + criteria);

        CriteriaQuery query = new CriteriaQuery(criteria);

        // 3. Détermination des classes et index
        Class<? extends AbstractUserNeed> needClass = resolveNeedClass(product);
        String indexName = getIndexNameFromClass(needClass);
        IndexCoordinates indexCoordinates = IndexCoordinates.of(indexName);

        System.out.println("📦 Index ciblé : " + indexName);
        System.out.println("📌 IndexCoordinates : " + indexCoordinates);

        // 4. Exécution de la recherche
        SearchHits<? extends AbstractUserNeed> searchHits = elasticsearchOperations.search(query, needClass, indexCoordinates);

        System.out.println("✅ Nombre de besoins correspondants trouvés : " + searchHits.getTotalHits());

        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    private String getIndexNameFromClass(Class<?> clazz) {
        String snakeCase = clazz.getSimpleName()
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase();
        return snakeCase.endsWith("s") ? snakeCase : snakeCase + "s";
    }

    @SuppressWarnings("unchecked")
    private Class<? extends AbstractUserNeed> resolveNeedClass(AbstractProduct product) {
        try {
            String productClassName = product.getClass().getSimpleName(); // ex: VehicleProduct
            String needClassName = productClassName.replace("Product", "Need"); // VehicleNeed
            String fullClassName = "com.mo.core.model.needs." + needClassName;

            Class<?> clazz = Class.forName(fullClassName);
            if (AbstractUserNeed.class.isAssignableFrom(clazz)) {
                return (Class<? extends AbstractUserNeed>) clazz;
            }
        } catch (ClassNotFoundException e) {
            log.warn("❗ Classe de besoin non trouvée pour : {}", product.getClass().getSimpleName(), e);
        }

        return AbstractUserNeed.class; // fallback générique
    }

}
