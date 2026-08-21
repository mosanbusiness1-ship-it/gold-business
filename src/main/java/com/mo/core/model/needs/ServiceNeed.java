package com.mo.core.model.needs;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.dtos.DurationToLongConverter;
import com.mo.core.visitors.need_visitors.UserNeedVisitor;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class ServiceNeed extends AbstractUserNeed {

    @JsonProperty("available_slots")
    private List<LocalDateTime> availableSlots;

    @JsonProperty("service_provider")
    private String serviceProvider;

    @JsonProperty("location")
    private String location;

    private Long duration;

    @JsonProperty("online_available")
    private Boolean onlineAvailable;

    @JsonProperty("available_after")
    private LocalDateTime availableAfter;

    @JsonProperty("service_area_radius_km")
    private Double serviceAreaRadiusKm;

    @JsonProperty("preferred_languages")
    private java.util.List<String> preferredLanguages = new java.util.ArrayList<>();

    @JsonProperty("minimum_provider_rating")
    private Double minimumProviderRating;

    @JsonProperty("cancellation_policy_preference")
    private String cancellationPolicyPreference;
    
    /**
     * Convertit la liste des availableSlots de LocalDateTime vers Instant.
     * Utile pour l'indexation dans Elasticsearch.
     */
//    public List<Instant> getAvailableSlotsAsInstants() {
//        return this.availableSlots != null
//                ? this.availableSlots.stream()
//                    .filter(Objects::nonNull)
//                    .map(dt -> dt.atZone(ZoneId.systemDefault()).toInstant())
//                    .collect(Collectors.toList())
//                : null;
//    }
//
//    /**
//     * Convertit le champ availableAfter de LocalDateTime vers Instant.
//     * Utile pour l'indexation dans Elasticsearch.
//     */
//    public Instant getAvailableAfterAsInstant() {
//        return this.availableAfter != null
//                ? this.availableAfter.atZone(ZoneId.systemDefault()).toInstant()
//                : null;
//    }

    @Override
    public <R> R accept(UserNeedVisitor<R> visitor) {
        return visitor.visit(this);
    }

}


