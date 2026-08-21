package com.mo.core.model.products;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.enums.RealEstateType;
import com.mo.core.visitors.product_visitors.ProductVisitor;

@Entity
@Table(name = "real_estate_products",
       indexes = {
           @Index(name = "idx_re_address", columnList = "address"),
           @Index(name = "idx_re_type", columnList = "realEstateType"),
           @Index(name = "idx_re_surface", columnList = "surfaceArea"),
           @Index(name = "idx_re_for_rent", columnList = "isForRent"),
           @Index(name = "idx_re_for_sale", columnList = "isForSale")
       })
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class RealEstateProduct extends AbstractProduct {

    
	
    @Column(nullable = false, length = 255)
    private String address;
    
    @Column(nullable = false, length = 255)
    private String city;
    
    @JsonProperty("surface_area")
    @Column(nullable = false)
    private Double surfaceArea;
    
    @JsonProperty("room_count")
    private Integer roomCount;

    @JsonProperty("bathroom_count")
    private Integer bathroomCount;
    
    @JsonProperty("real_estate_type")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RealEstateType realEstateType;
    
    @JsonProperty("is_for_rent")
    private Boolean isForRent;

    @JsonProperty("is_for_sale")
    private Boolean isForSale;
    
    @JsonProperty("construction_year")
    private Integer constructionYear;
    
    @Column(length = 10)
    @JsonProperty("energy_class")
    private String energyClass;
    
    private Integer floor;
    
    private Boolean balcony;
    
    private Boolean furnished;
    
    @JsonProperty("hoa_fees")
    private java.math.BigDecimal hoaFees;
    
    private String parking; // NONE, STREET, GARAGE, COVERED
    
    @JsonProperty("energy_rating_numeric")
    private Integer energyRatingNumeric;
    
    @JsonProperty("neighborhood_tags")
    @ElementCollection
    @CollectionTable(name = "re_neighborhood_tags", joinColumns = @JoinColumn(name = "property_id"))
    @Column(name = "tag")
    private List<String> neighborhoodTags = new ArrayList<>();

    @Override
    public <T> T accept(ProductVisitor<T> visitor) {
        return visitor.visit(this);
    }
}