package com.mo.core.documents.organisations;
import com.mo.core.enums.OrganisationType;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Document(indexName = "organisation_documents")
public class OrganisationDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Keyword)
    private String name;

    @Field(type = FieldType.Keyword)
    private OrganisationType type;

    // Parent organisation stored as ID reference
    @Field(type = FieldType.Long)
    private Long parentId;

    // Owner stored as ID reference (User)
    @Field(type = FieldType.Long)
    private Long ownerId;

    // Children organisations - stored as list of IDs or as nested objects (ici ID)
    @Field(type = FieldType.Long)
    @Builder.Default
    private List<Long> childrenIds = new ArrayList<>();

    // Products stored as IDs (pas d'imbrication)
    @Field(type = FieldType.Long)
    @Builder.Default
    private List<Long> productIds = new ArrayList<>();

    
    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date)
    private LocalDateTime updatedAt;

    // Pas de @PrePersist ni @PreUpdate ici, car Elasticsearch ne gère pas cela

}







