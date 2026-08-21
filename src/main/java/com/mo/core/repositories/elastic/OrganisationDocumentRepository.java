package com.mo.core.repositories.elastic;

import java.util.List;
import java.util.Optional;

import org.springframework.data.elasticsearch.annotations.Query;

import com.mo.core.documents.organisations.OrganisationDocument;
import com.mo.core.enums.OrganisationType;
import com.mo.core.model.organisations.Organisation;

public interface OrganisationDocumentRepository {

    // FIND METHODS BASIQUES
    Optional<Organisation> findByName(String name);

    List<Organisation> findByType(OrganisationType type);

    List<Organisation> findByOwnerId(Long ownerId);

    List<Organisation> findByParentId(Long parentId);

    List<Organisation> findByParentIdIsNull();

    // EXISTS METHODS
    boolean existsByName(String name);

    boolean existsByParentId(Long parentId);

    // COUNT METHODS
    long countByType(OrganisationType type);

    // ADVANCED FILTERING (Exemple avec Elasticsearch Query DSL)
    @Query("""
        {
          "bool": {
            "must": [
              { "match": { "type": "?0" } },
              { "match": { "name": "?1" } }
            ]
          }
        }
        """)
    List<Organisation> searchByTypeAndName(OrganisationType type, String name);

    // NOTE : Pour les relations comme products ou children, il faut les indexer en tant que champs imbriqués (nested)
    // ou dans un champ `allText` concaténé.
}



