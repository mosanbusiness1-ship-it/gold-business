package com.mo.core.model.products;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.visitors.product_visitors.ProductVisitor;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.mo.core.enums.VehicleType;

@Entity
@Table(name = "vehicle_products")
@Data
@EqualsAndHashCode(callSuper = true)
public class VehicleProduct extends AbstractProduct {

    @Enumerated(EnumType.STRING)
    @JsonProperty("vehicle_type")
    private VehicleType vehicleType;
    
    private String make;
    private String model;
    
    @JsonProperty("manufacturing_year")
    private Integer manufacturingYear;
    
    private Double mileage;

    @JsonProperty("fuel_type")
    private String fuelType;

    private String color;
    
    @JsonProperty("vin_number")
    private String vinNumber;
    
    private String transmission; // AUTO, MANUAL, CVT
    
    private String trim;
    
    @JsonProperty("fuel_consumption_l_per_100km")
    private Double fuelConsumptionLPer100km;
    
    private Integer doors;
    
    @JsonProperty("vehicle_condition")
    private String vehicleCondition; // NEW, USED, CPO
    
    @JsonProperty("warranty_months")
    private Integer warrantyMonths;
    
    @JsonProperty("seller_rating")
    private Double sellerRating;

    @Override
    public <T> T accept(ProductVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
