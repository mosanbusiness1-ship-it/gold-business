package com.mo.core.model.needs;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.enums.FoodCategory;
import com.mo.core.visitors.need_visitors.UserNeedVisitor;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public
class FoodNeed extends AbstractUserNeed {

    @Enumerated(EnumType.STRING)
    @JsonProperty("food_category")
    private FoodCategory foodCategory;

    @JsonProperty("expiry_Date")
    private java.time.LocalDateTime expiryDate;

    @JsonProperty("nutritional_info")
    private String nutritionalInfo;
    private Boolean organic;

    @JsonProperty("gluten_Free")
    private Boolean glutenFree;
    private Double weight;
    
    @JsonProperty("dietary_restrictions")
    @jakarta.persistence.ElementCollection
    @jakarta.persistence.CollectionTable(name = "food_need_restrictions", joinColumns = @jakarta.persistence.JoinColumn(name = "need_id"))
    @jakarta.persistence.Column(name = "restriction")
    private java.util.List<String> dietaryRestrictions = new java.util.ArrayList<>();
    
    @JsonProperty("min_shelf_life_days")
    private Integer minShelfLifeDays;
    
    @JsonProperty("preferred_origin")
    private String preferredOrigin;
    
    @JsonProperty("delivery_temperature_required")
    private String deliveryTemperatureRequired;

    @Override
    public <R> R accept(UserNeedVisitor<R> visitor) {
        return visitor.visit(this);
    }

}
