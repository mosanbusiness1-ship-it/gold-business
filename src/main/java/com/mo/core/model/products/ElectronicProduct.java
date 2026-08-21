package com.mo.core.model.products;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.enums.ElectronicType;
import com.mo.core.visitors.product_visitors.ProductVisitor;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "electronic_products")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ElectronicProduct extends AbstractProduct {

    @Enumerated(EnumType.STRING)
    @JsonProperty("electronic_type")
    private ElectronicType electronicType;

    private String brand;
    private String model;
    private String specifications; // JSON string or could be @Embedded

    @JsonProperty("warranty_period")
    private String warrantyPeriod;
    
    @JsonProperty("release_year")
    private Integer releaseYear;
    
    @JsonProperty("battery_health_percent")
    private Integer batteryHealthPercent;
    
    @JsonProperty("accessories_included")
    private java.util.List<String> accessoriesIncluded = new java.util.ArrayList<>();
    
    @JsonProperty("supported_networks")
    private java.util.List<String> supportedNetworks = new java.util.ArrayList<>();
    
    @JsonProperty("warranty_months")
    private Integer warrantyMonths;
    
    @JsonProperty("seller_rating")
    private Double sellerRating;
    
	@Override
	public <T> T accept(ProductVisitor<T> visitor) {
        return visitor.visit(this);
    }
}