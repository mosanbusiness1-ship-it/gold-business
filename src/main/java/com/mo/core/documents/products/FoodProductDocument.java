package com.mo.core.documents.products;

import com.mo.core.enums.FoodCategory;
import com.mo.core.enums.ProductType;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Setting(settingPath = "/elasticsearch/settings.json")
@Mapping(mappingPath = "/elasticsearch/mappings/food-product.json")
@Document(indexName = "food_product_documents")
public class FoodProductDocument extends AbstractProductDocument {

    @Field(type = FieldType.Keyword, name = "category")
    private FoodCategory category;

    @Field(type = FieldType.Date, name = "expiry_date")
    private LocalDate expiryDate;

    @Field(type = FieldType.Text, name = "nutritional_info")
    private String nutritionalInfo;

    @Field(type = FieldType.Boolean, name = "organic")
    private Boolean organic;

    @Field(type = FieldType.Boolean, name = "gluten_free")
    private Boolean glutenFree;

    @Field(type = FieldType.Double, name = "weight")
    private Double weight;

    @Field(type = FieldType.Keyword, name = "origin_country")
    private String originCountry;

    @Field(type = FieldType.Keyword, name = "organic_cert_id")
    private String organicCertId;

    @Field(type = FieldType.Keyword, name = "allergen_tags")
    private List<String> allergenTags;

    @Field(type = FieldType.Keyword, name = "packaging_type")
    private String packagingType;

    @Field(type = FieldType.Integer, name = "shelf_life_days")
    private Integer shelfLifeDays;

    @Field(type = FieldType.Keyword, name = "storage_temperature")
    private String storageTemperature;

    @Field(type = FieldType.Text, analyzer = "standard", name = "all_text")
    private String allText;
}
