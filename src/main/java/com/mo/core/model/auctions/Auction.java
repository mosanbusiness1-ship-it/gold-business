package com.mo.core.model.auctions;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;


import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.model.auctions.Bid;
import com.mo.core.model.needs.AbstractUserNeed;
import com.mo.core.model.products.AbstractProduct;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "cautions")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "need_id", nullable = false, unique = true)
    private AbstractUserNeed need;

    @ManyToMany
    @JoinTable(
        name = "caution_products",
        joinColumns = @JoinColumn(name = "caution_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id"),
        uniqueConstraints = @UniqueConstraint(
            name = "uk_caution_product",
            columnNames = {"caution_id", "product_id"}
        )
    )
    @JsonIgnore
    private Set<AbstractProduct> products = new HashSet<>();

    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Bid> bids = new HashSet<>();

    //@JsonProperty("started_at")
    private LocalDateTime startedAt;

    //@JsonProperty("end_at")
    private LocalDateTime endAt;

    private boolean isActived;

    @CreationTimestamp
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version; // verrouillage optimiste pour éviter les conflits de concurrence

    /** Enchère ouverte ? */
    @JsonIgnore
    public boolean isOpen() {
        LocalDateTime now = LocalDateTime.now();
        boolean afterStart = (startedAt == null) || !now.isBefore(startedAt);
        boolean beforeEnd  = (endAt == null)    || !now.isAfter(endAt);
        return isActived && afterStart && beforeEnd;
    }
}
