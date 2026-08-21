package com.mo.core.services;

import org.springframework.stereotype.Service;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;

import com.mo.core.model.needs.AbstractUserNeed;
import com.mo.core.model.products.VehicleProduct;

import java.util.List;

@Service
public class NeedMatchingSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    public NeedMatchingSearchService(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public List<AbstractUserNeed> matchVehicleNeeds(VehicleProduct product) {
        // Création des critères de recherche
        Criteria criteria = new Criteria("productType").is("VEHICLE");

        if (product.getMake() != null) {
            criteria = criteria.and(new Criteria("make").is(product.getMake()));
        }

        if (product.getModel() != null) {
            criteria = criteria.and(new Criteria("model").is(product.getModel()));
        }

        if (product.getManufacturingYear() != null) {
            criteria = criteria.and(new Criteria("minYear").lessThanEqual(product.getManufacturingYear()));
        }

        if (product.getPrice() != null) {
            criteria = criteria.and(new Criteria("maxPrice").greaterThanEqual(product.getPrice()));
        }

        // Construction de la requête CriteriaQuery
        CriteriaQuery query = new CriteriaQuery(criteria);

        // Exécution de la requête
        SearchHits<AbstractUserNeed> searchHits = elasticsearchOperations.search(query, AbstractUserNeed.class);

        // Extraction des résultats
        return searchHits.stream()
                .map(SearchHit::getContent)
                .toList();
    }
}

