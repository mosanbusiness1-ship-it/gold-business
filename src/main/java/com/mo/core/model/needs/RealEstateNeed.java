package com.mo.core.model.needs;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.enums.RealEstateType;
import com.mo.core.visitors.need_visitors.UserNeedVisitor;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class RealEstateNeed extends AbstractUserNeed {

    @Column(length = 255)
    private String address;
    private String city;

    @JsonProperty("surface_area")
    private Double surfaceArea;

    @JsonProperty("room_count")
    private Integer roomCount;

    @JsonProperty("bathroom_count")
    private Integer bathroomCount;

    @Enumerated(EnumType.STRING)
    @JsonProperty("real_estate_type")
    private RealEstateType realEstateType;

    @JsonProperty("is_for_rent")
    private Boolean isForRent;

    @JsonProperty("is_for_sale")
    private Boolean isForSale;

    @JsonProperty("construction_year")
    private Integer constructionYear;

    @JsonProperty("energy_class")
    private String energyClass;
    
    @JsonProperty("move_in_date")
    private java.time.LocalDate moveInDate;
    
    @JsonProperty("max_hoa_fee")
    private java.math.BigDecimal maxHOAFee;
    
    @JsonProperty("min_bedrooms")
    private Integer minBedrooms;
    
    @JsonProperty("preferred_neighborhoods")
    @jakarta.persistence.ElementCollection
    @jakarta.persistence.CollectionTable(name = "re_need_neighborhoods", joinColumns = @jakarta.persistence.JoinColumn(name = "need_id"))
    @jakarta.persistence.Column(name = "neighborhood")
    private java.util.List<String> preferredNeighborhoods = new java.util.ArrayList<>();
    
    @JsonProperty("school_district")
    private String schoolDistrict;
    
    @JsonProperty("pet_friendly")
    private Boolean petFriendly = true;

    @Override
    public <R> R accept(UserNeedVisitor<R> visitor) {
        return visitor.visit(this);
    }

}
