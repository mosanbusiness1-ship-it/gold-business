package com.mo.core.model.products;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.enums.FashionType;
import com.mo.core.enums.SizeSystem;
import com.mo.core.visitors.product_visitors.ProductVisitor;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "fashion_products")
@Data
@EqualsAndHashCode(callSuper = true)
public class FashionProduct extends AbstractProduct {

    @Enumerated(EnumType.STRING)
    private FashionType fashionType;

    private String size;

    @JsonProperty("size_system")
    private SizeSystem sizeSystem;

    private String color;
    private String material;
    private String brand;

    @JsonProperty("target_gender")
    private String targetGender; // "Men", "Women", "Unisex"
    
    private String condition; // NEW, LIKE_NEW, VINTAGE
    
    @JsonProperty("sustainable_certifications")
    private java.util.List<String> sustainableCertifications = new java.util.ArrayList<>();
    
    @JsonProperty("size_fit")
    private String sizeFit; // SLIM, REGULAR, LOOSE
    
    @JsonProperty("material_origin")
    private String materialOrigin;

    @Override
    public <T> T accept(ProductVisitor<T> visitor) {
        return visitor.visit(this);
    }
}