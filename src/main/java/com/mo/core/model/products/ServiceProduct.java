package com.mo.core.model.products;

import jakarta.persistence.*;
import lombok.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.visitors.product_visitors.ProductVisitor;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "service_products",
       indexes = {
           @Index(name = "idx_service_provider", columnList = "serviceProvider"),
           @Index(name = "idx_service_online", columnList = "onlineAvailable"),
           @Index(name = "idx_service_location", columnList = "location")
       })
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class ServiceProduct extends AbstractProduct {
    
    @Column(length = 100)
    @JsonProperty("service_provider")
    private String serviceProvider;
    
    @Column(length = 100)
    
    private String location;
    
    @Column
    private Long duration;
    
    @JsonProperty("online_available")
    private Boolean onlineAvailable;
    
    @JsonProperty("available_after")
    private LocalDateTime availableAfter;
    
    @ElementCollection
    @CollectionTable(name = "service_availability_slots", 
                    joinColumns = @JoinColumn(name = "product_id"),
                    indexes = @Index(name = "idx_service_slots_product", columnList = "product_id"))
    @Column(name = "slot_time")
 
    @JsonProperty("available_slots")
    private List<LocalDateTime> availableSlots = new ArrayList<>();
    
    @JsonProperty("service_area_radius_km")
    private Double serviceAreaRadiusKm;
    
    @JsonProperty("cancellation_policy")
    private String cancellationPolicy;
    
    @JsonProperty("languages_spoken")
    private List<String> languagesSpoken = new ArrayList<>();
    
    @JsonProperty("provider_rating")
    private Double providerRating;
    
    /**
     * Convertit la liste des availableSlots de LocalDateTime vers Instant.
     * Utile pour l'indexation dans Elasticsearch.
     */
    public List<Instant> getAvailableSlotsAsInstants() {
        return this.availableSlots != null
                ? this.availableSlots.stream()
                    .filter(Objects::nonNull)
                    .map(dt -> dt.atZone(ZoneId.systemDefault()).toInstant())
                    .collect(Collectors.toList())
                : null;
    }

    /**
     * Convertit le champ availableAfter de LocalDateTime vers Instant.
     * Utile pour l'indexation dans Elasticsearch.
     */
    public Instant getAvailableAfterAsInstant() {
        return this.availableAfter != null
                ? this.availableAfter.atZone(ZoneId.systemDefault()).toInstant()
                : null;
    }

    @Override
    public <T> T accept(ProductVisitor<T> visitor) {
        return visitor.visit(this);
    }
}