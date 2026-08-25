package com.mo.core.model.products;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.mo.auth.User;
import com.mo.core.enums.Currency;
import com.mo.core.enums.ProductType;
import com.mo.core.visitors.product_visitors.ProductVisitor;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY, // On utilise la propriété "type"
    property = "type",
    visible = true // Nécessaire pour que Jackson passe la valeur à l'objet
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ServiceProduct.class, name = "SERVICE"),
    @JsonSubTypes.Type(value = VehicleProduct.class, name = "VEHICLE"),
    @JsonSubTypes.Type(value = ElectronicProduct.class, name = "ELECTRONIC"),
    @JsonSubTypes.Type(value = FashionProduct.class, name = "FASHION"),
    @JsonSubTypes.Type(value = FoodProduct.class, name = "FOOD"),
    @JsonSubTypes.Type(value = RealEstateProduct.class, name = "REALESTATE")
})

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Data
public abstract class AbstractProduct {

    @Id
    @ToString.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

//    @JsonIgnore
    @ToString.Include
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ToString.Include
    @NotBlank(message = "Le nom du produit est requis.")
    @Column(nullable = false, length = 100)
    protected String name;

    @ToString.Include
    @Column(columnDefinition = "TEXT")
    protected String description;

    @ToString.Include
    @Positive(message = "Le prix doit être positif.")
    @Column(nullable = false, precision = 10, scale = 2)
    protected BigDecimal price;
    
    @ToString.Include
    private Currency currency;
    
    @ToString.Include
    private int quantity;

  
    @ToString.Include
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    protected ProductType type;

    @ToString.Include
    @ElementCollection
    @CollectionTable(name = "product_photos", 
                    joinColumns = @JoinColumn(name = "product_id"),
                    indexes = @Index(name = "idx_photos_product", columnList = "product_id"))
    @Column(name = "photo_url", length = 512)
  
    @JsonProperty("photo_urls")
    private List<String> photoUrls = new ArrayList<>();
    
    @ToString.Include
    @JsonProperty("created_at")
    @CreationTimestamp
    protected LocalDateTime createdAt;
    
    @ToString.Include
    @JsonProperty("updated_at")
    @UpdateTimestamp
    protected LocalDateTime updatedAt;

    @ToString.Include
    @Column(nullable = false)
    protected boolean enabled = true;

    @ToString.Include
    @Column(nullable = false)
    private boolean isPlatformOwner = false;


    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean certified = false;


    @ToString.Include
    @Column(nullable = false)
    protected int version = 1; // Champ version pour le versionnement

    // Getter et Setter pour le champ version
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

        public <T> T accept(ProductVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

