package com.mo.core.model.needs;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.mo.auth.User;
import com.mo.core.documents.needs.AbstractUserNeedDocument;
import com.mo.core.enums.Currency;
import com.mo.core.enums.NeedType;
import com.mo.core.enums.ProductType;
import com.mo.core.model.organisations.Organisation;
import com.mo.core.model.products.AbstractProduct;
import com.mo.core.visitors.need_visitors.UserNeedVisitor;
import com.mo.core.visitors.product_visitors.ProductVisitor;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ServiceNeed.class, name = "SERVICE"),
        @JsonSubTypes.Type(value = VehicleNeed.class, name = "VEHICLE"),
        @JsonSubTypes.Type(value = ElectronicNeed.class, name = "ELECTRONIC"),
        @JsonSubTypes.Type(value = FashionNeed.class, name = "FASHION"),
        @JsonSubTypes.Type(value = FoodNeed.class, name = "FOOD"),
        @JsonSubTypes.Type(value = RealEstateNeed.class, name = "REALESTATE")
})
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
//@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Data
public abstract class AbstractUserNeed {


    @Id
    @ToString.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Include
    private String name;

    @ToString.Include
    @JsonProperty("max_price")
    private BigDecimal maxPrice;
    
    @ToString.Include
    private Currency currency;
    
    @ToString.Include
    private int quantity;

    
    @ToString.Include
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ToString.Include
    private String description;
    

    // Champ indispensable pour la désérialisation polymorphe avec EXISTING_PROPERTY
    @ToString.Include
    @Enumerated(EnumType.STRING)
    @Column(name = "need_type")
    private NeedType type;
    
    @ToString.Include
    @JsonProperty("auto_purchase")
    private boolean autoPurchase;
    

    @ToString.Include
    @JsonProperty("photo_urls")
    private List<String> photoUrls = new ArrayList<>();
    
    @ToString.Include
    @JsonProperty("notify_similar_products")
    private boolean notifySimilarProducts = true;
    
    @ToString.Include
    @JsonProperty("mandatory_fields")
    private List<String> mandatoryFields = new ArrayList<>();
    
    
    @ManyToMany(mappedBy = "needs")
    @JsonIgnore
    private Set<Organisation> organisations = new HashSet<>();


    @ToString.Include
    @JsonProperty("created_at")
    @CreationTimestamp
    protected LocalDateTime createdAt;
    
    @ToString.Include
    @JsonProperty("updated_at")
    @UpdateTimestamp
    protected LocalDateTime updatedAt;
    
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractUserNeed userNeed = (AbstractUserNeed) o;
        return Objects.equals(id, userNeed.id); // ou comparer plus de champs
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // idem, à adapter
    }
    
    public void addOrganisation(Organisation organisation) {
        this.organisations.add(organisation);
        organisation.getNeeds().add(this);
    }

    public void removeOrganisation(Organisation organisation) {
        this.organisations.remove(organisation);
        organisation.getNeeds().remove(this);
    }


    

    //public abstract <R> R accept(UserNeedVisitor<R> visitor);

    public <T> T accept(UserNeedVisitor<T> visitor) {
        return visitor.visit(this);
        
        
}
}