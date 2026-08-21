package com.mo.core.model.organisations;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "organisation_rating_summary")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrganisationRatingSummary {

    @Id
    @Column(name = "organisation_id")
    private Long organisationId;

    @ManyToOne
    @MapsId
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    // Average of org-assigned scores across products
    private Double averageOrgScore;

    // Average of customer scores across products
    private Double averageCustomerScore;

    private Integer totalProductsScored;

    private Integer totalCustomerReviews;

    private Integer verifiedPurchaseReviews;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
