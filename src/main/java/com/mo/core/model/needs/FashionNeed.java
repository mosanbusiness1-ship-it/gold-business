package com.mo.core.model.needs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.enums.SizeSystem;
import com.mo.core.enums.FashionType;
import com.mo.core.visitors.need_visitors.UserNeedVisitor;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("FASHION")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public
class FashionNeed extends AbstractUserNeed {

    @Enumerated(EnumType.STRING)

    @JsonProperty("fashion_type")
    private FashionType fashionType;

    @JsonProperty("fashion_brand")
    private String fashionBrand;
    private String size;

    @Enumerated(EnumType.STRING)
    @JsonProperty("size_System")
    private SizeSystem sizeSystem;

    @JsonProperty("fashion_Color")
    private String fashionColor;
    private String material;

    @JsonProperty("target_gender")
    private String targetGender;
    
    @JsonProperty("preferred_brands")
    @jakarta.persistence.ElementCollection
    @jakarta.persistence.CollectionTable(name = "fashion_need_brands", joinColumns = @jakarta.persistence.JoinColumn(name = "need_id"))
    @jakarta.persistence.Column(name = "brand")
    private java.util.List<String> preferredBrands = new java.util.ArrayList<>();
    
    @JsonProperty("fit_preference")
    private String fitPreference; // SLIM, REGULAR, LOOSE
    
    @JsonProperty("material_preference")
    private String materialPreference;
    
    @JsonProperty("style_tags")
    @jakarta.persistence.ElementCollection
    @jakarta.persistence.CollectionTable(name = "fashion_need_styles", joinColumns = @jakarta.persistence.JoinColumn(name = "need_id"))
    @jakarta.persistence.Column(name = "style")
    private java.util.List<String> styleTags = new java.util.ArrayList<>();
    
    @JsonProperty("gender_neutral")
    private Boolean genderNeutral = false;

    @Override
    public <R> R accept(UserNeedVisitor<R> visitor) {
        return visitor.visit(this);
    }

}

