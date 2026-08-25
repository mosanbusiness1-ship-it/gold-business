package com.mo.core.model.organisations;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.*;
import com.mo.auth.User;
import com.mo.core.enums.CommissionMode;
import com.mo.core.enums.MemberStatus;
import com.mo.core.enums.MemberType;
import com.mo.core.enums.NeedType;
import com.mo.core.enums.OrganisationVisibility;
import com.mo.core.enums.OrganisationStatus;
import com.mo.core.enums.OrganisationType;
import com.mo.core.enums.ProductType;
import com.mo.core.model.needs.AbstractUserNeed;
import com.mo.core.model.products.AbstractProduct;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.math.BigDecimal;

import com.mo.core.enums.OrganisationAdvantage;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "organisation", 
       indexes = {
           @Index(name = "idx_organisation_name", columnList = "name", unique = true),
           @Index(name = "idx_organisation_type", columnList = "type"),
           @Index(name = "idx_organisation_parent", columnList = "parent_id"),
           @Index(name = "idx_organisation_owner", columnList = "owner_id"),
           @Index(name = "idx_organisation_created_at", columnList = "createdAt")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Organisation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private OrganisationType type;
    
    @Column(length = 1000)
    private String logoUrl;

    @Column(nullable = false, length = 100)
    private String category; // phone, food, pc, fashion, realestate, service, etc.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OrganisationVisibility visibility = OrganisationVisibility.PRIVATE; // visibility: PUBLIC / PRIVATE / PROTECTED
    
    private String joinToken; // UUID ou token généré pour l’invitation

    private boolean publicJoin; // lien accessible publiquement ou non

    private boolean requiresApproval; // demande d'approbation ou non
    
    private boolean restrictedToAdminsOnly;// seul les admins y publient
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "fk_organisation_parent"))
    private Organisation parent;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", foreignKey = @ForeignKey(name = "fk_organisation_owner"))
    private User owner;
    
    @JsonIgnore
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Organisation> children = new ArrayList<>();
    
    
    @JsonBackReference
    @JsonIgnore
    @ManyToMany
    @JoinTable(
        name = "organisation_product",
        joinColumns = @JoinColumn(name = "organisation_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    @Builder.Default
    private Set<AbstractProduct> products = new HashSet<>();
    
    @ManyToMany
    @JoinTable(
        name = "organisation_need",
        joinColumns = @JoinColumn(name = "organisation_id"),
        inverseJoinColumns = @JoinColumn(name = "need_id")
    )
    @Builder.Default
    private Set<AbstractUserNeed> needs = new HashSet<>();

    
    @OneToMany(mappedBy = "organisation", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @JsonBackReference
    @Builder.Default
    private List<OrganisationMember> memberships = new ArrayList<>();
    
    // Trust & metadata fields
    @Builder.Default
    private boolean verified = false;

    @ElementCollection(targetClass = OrganisationAdvantage.class)
    @CollectionTable(name = "organisation_advantages", joinColumns = @JoinColumn(name = "organisation_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "advantage", length = 50)
    @Builder.Default
    private Set<OrganisationAdvantage> advantages = new HashSet<>();

    @Column(precision = 5, scale = 2)
    private BigDecimal commissionPercent;

    private Integer trustLevel; // e.g., 0-100 or 1-5 depending on UI

    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OrganisationStatus status = OrganisationStatus.PENDING;

    @ElementCollection(targetClass = ProductType.class)
    @CollectionTable(name = "organisation_supported_product_types", joinColumns = @JoinColumn(name = "organisation_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", length = 50)
    @Builder.Default
    private Set<ProductType> supportedProductTypes = new HashSet<>();

    @ElementCollection(targetClass = NeedType.class)
    @CollectionTable(name = "organisation_supported_need_types", joinColumns = @JoinColumn(name = "organisation_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "need_type", length = 50)
    @Builder.Default
    private Set<NeedType> supportedNeedTypes = new HashSet<>();

    @Column(precision = 5, scale = 2)
    private BigDecimal commissionOnPublish;

    @Column(precision = 5, scale = 2)
    private BigDecimal commissionOnSale;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CommissionMode commissionMode;

    @Builder.Default
    private boolean offersGuarantee = false;

    private String city;

    private String country;

    private Double latitude;

    private Double longitude;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public void addMember(User user, MemberType type, Set<String> roles) {
        OrganisationMember membership = new OrganisationMember();
        membership.setOrganisation(this);
        membership.setUser(user);
        membership.setType(type);
        membership.setRoles(roles);
        membership.setJoinedAt(LocalDateTime.now());
        membership.setStatus(MemberStatus.ACTIVE);
        
        // Mise à jour bidirectionnelle
        this.memberships.add(membership);
        user.getMemberships().add(membership); // ⬅️ Ajouter cette ligne
    }
    
    public Optional<OrganisationMember> getMembershipForUser(Long userId) {
        return memberships.stream()
            .filter(m -> m.getUser().getId().equals(userId))
            .findFirst();
    }

    public boolean hasUserWithRole(Long userId, String role) {
        return memberships.stream()
            .filter(m -> m.getUser().getId().equals(userId))
            .anyMatch(m -> m.getRoles().contains(role));
    }
    
    public void addNeed(AbstractUserNeed need) {
        this.needs.add(need);
        need.getOrganisations().add(this);
    }

    public void removeNeed(AbstractUserNeed need) {
        this.needs.remove(need);
        need.getOrganisations().remove(this);
    }
}






