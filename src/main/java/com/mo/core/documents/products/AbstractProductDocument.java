package com.mo.core.documents.products;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.mo.auth.User;
import com.mo.core.enums.Currency;
import com.mo.core.enums.ProductType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor(force=true)
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
//@Document(indexName = "products")
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ServiceProductDocument.class, name = "SERVICEPRODUCT"),
    @JsonSubTypes.Type(value = VehicleProductDocument.class, name = "VEHICLEPRODUCT"),
    @JsonSubTypes.Type(value = ElectronicProductDocument.class, name = "ELECTRONICPRODUCT"),
    @JsonSubTypes.Type(value = FashionProductDocument.class, name = "FASHIONPRODUCT"),
    @JsonSubTypes.Type(value = FoodProductDocument.class, name = "FOODPRODUCT"),
    @JsonSubTypes.Type(value = RealEstateProductDocument.class, name = "REALESTATEPRODUCT")
})
public abstract class AbstractProductDocument {

    @Id
    @Field(type = FieldType.Long, name = "id")
    private Long id;

    @Field(type = FieldType.Long, name = "owner_id")
    private Long ownerId;

    @Field(type = FieldType.Text, analyzer = "standard", name = "name")
    private String name;

    @Field(type = FieldType.Text, analyzer = "standard", name = "description")
    private String description;

    @Field(type = FieldType.Double, name = "price")
    private BigDecimal price;
    
    @Field(type = FieldType.Keyword, name = "currency")
    private Currency currency;

    @Field(type = FieldType.Keyword, name = "type")
    private ProductType type;

    @Field(type = FieldType.Keyword, name = "photo_urls")
    private List<String> photoUrls = new ArrayList<>();

    @Field(type = FieldType.Date, format = DateFormat.date_time, name = "created_at")
    private Instant createdAt;

    @Field(type = FieldType.Date, format = DateFormat.date_time, name = "updated_at")
    private Instant updatedAt;

    @Field(type = FieldType.Boolean, name = "enabled")
    private boolean enabled = true;

    @Field(type = FieldType.Boolean, name = "is_platform_owner")
    private boolean isPlatformOwner = false;

    @Field(type = FieldType.Integer, name = "version")
    private int version = 1;

    @Field(type = FieldType.Boolean, name = "certified")
    private boolean certified = false;

    @Field(type = FieldType.Date, format = DateFormat.date_time, name = "indexed_at")
    private Instant indexedAt;

    @Field(type = FieldType.Keyword, name = "index_status")
    private String indexStatus;

    @Field(type = FieldType.Double, name = "business_priority")
    private Double businessPriority;

    @Field(type = FieldType.Double, name = "quality_score")
    private Double qualityScore;

    @Field(type = FieldType.Keyword, name = "business_tags")
    private List<String> businessTags = new ArrayList<>();

    @Field(type = FieldType.Text, name = "all_text")
    private String allText;
}

