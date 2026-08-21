package com.mo.core.documents.needs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import com.mo.core.documents.products.AbstractProductDocument;
import com.mo.core.enums.Currency;
import com.mo.core.enums.NeedType;
import com.mo.core.enums.ProductType;
import com.mo.core.visitors.searchVisitors.UserNeedVisitor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
//@Document(indexName = "needs")
public abstract class AbstractUserNeedDocument {

    @Id
    @Field(type = FieldType.Long, name = "id")
    private Long id;

    @Field(type = FieldType.Long, name = "user_id")
    @JsonProperty("user_id")
    private Long userId;

    @Field(type = FieldType.Text, analyzer = "standard", name = "name")
    @JsonProperty("name")
    private String name;

    @Field(type = FieldType.Text, analyzer = "standard", name = "description")
    @JsonProperty("description")
    private String description;

    @Field(type = FieldType.Double, name = "max_price")
    @JsonProperty("max_price")
    private BigDecimal maxPrice;
    
    @Field(type = FieldType.Text, analyzer = "standard", name = "currency")
    private Currency currency;

    @Field(type = FieldType.Keyword, name = "quantity")
    private int quantity;
    
    @Field(type = FieldType.Keyword, name = "type")
    @JsonProperty("type")
    private NeedType type;
    
    @Field(type = FieldType.Keyword, name = "auto_purchase")
    @JsonProperty("auto_purchase")
    private boolean autoPurchase;
    
    
    @Field(type = FieldType.Keyword, name = "photo_urls")
    @JsonProperty("photo_urls")
    private List<String> photoUrls = new ArrayList<>();

    @Field(type = FieldType.Date, format = DateFormat.date_time, name = "created_at")
    @JsonProperty("created_at")
    private Instant createdAt;

    @Field(type = FieldType.Date, format = DateFormat.date_time, name = "updated_at")
    @JsonProperty("updated_at")
    private Instant updatedAt;

    @Field(type = FieldType.Boolean, name = "enabled")
    @JsonProperty("enabled")
    private boolean enabled = true;

    @Field(type = FieldType.Date, format = DateFormat.date_time, name = "indexed_at")
    @JsonProperty("indexed_at")
    private Instant indexedAt;

    @Field(type = FieldType.Keyword, name = "index_status")
    @JsonProperty("index_status")
    private String indexStatus;

    @Field(type = FieldType.Double, name = "business_priority")
    @JsonProperty("business_priority")
    private Double businessPriority;

    @Field(type = FieldType.Keyword, name = "business_tags")
    @JsonProperty("business_tags")
    private List<String> businessTags = new ArrayList<>();

    @Field(type = FieldType.Boolean, name = "is_platform_owner")
    @JsonProperty("is_platform_owner")
    private boolean isPlatformOwner = false;

    @Field(type = FieldType.Integer, name = "version")
    @JsonProperty("version")
    private int version = 1;

    @Field(type = FieldType.Text, name = "all_text")
    @JsonProperty("all_text")
    private String allText;
    
 // 🔔 Nouveau champ : notification pour produits similaires
    @Field(type = FieldType.Boolean, name = "notify_similar_products")
    @JsonProperty("notify_similar_products")
    private boolean notifySimilarProducts = true;
    
    
    @Field(type = FieldType.Keyword, name = "mandatory_fields")
    @JsonProperty("mandatory_fields")
    private List<String> mandatoryFields = new ArrayList<>();
    
    @Field(type = FieldType.Keyword, name = "important_fields")
    @JsonProperty("important_fields")
    private List<String> importantFields = new ArrayList<>();
    
    

}
