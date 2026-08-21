package com.mo.core.repositories.elastic;
import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;

import com.mo.core.documents.products.AbstractProductDocument;
import com.mo.core.model.products.AbstractProduct;

public interface ProductSearchDocumentRepository {

    List<AbstractProduct> findByNameContaining(String name);

    // 🔍 Méthode full-text sur le champ "allText"
    @Query("{\"match\": {\"allText\": {\"query\": \"?0\"}}}")
    List<AbstractProduct> searchByAllText(String query);
}
