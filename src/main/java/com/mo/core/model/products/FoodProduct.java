package com.mo.core.model.products;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.enums.FoodCategory;
import com.mo.core.visitors.product_visitors.ProductVisitor;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "food_products")
@Data
@EqualsAndHashCode(callSuper = true)
public class FoodProduct extends AbstractProduct {


    @Enumerated(EnumType.STRING)
    private FoodCategory category;

    @JsonProperty("expiry_date")
    private LocalDate expiryDate;

    @JsonProperty("nutritional_info")
    private String nutritionalInfo; //json

    private Boolean organic;
    
    @JsonProperty("gluten_free")
    private Boolean glutenFree;

    private Double weight;
    
    @JsonProperty("origin_country")
    private String originCountry;
    
    @JsonProperty("organic_cert_id")
    private String organicCertId;
    
    @JsonProperty("allergen_tags")
    private java.util.List<String> allergenTags = new java.util.ArrayList<>();
    
    @JsonProperty("packaging_type")
    private String packagingType;
    
    @JsonProperty("shelf_life_days")
    private Integer shelfLifeDays;
    
    @JsonProperty("storage_temperature")
    private String storageTemperature;

    @Override
    public <T> T accept(ProductVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

