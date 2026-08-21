package com.mo.core.model.organisations;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.mo.auth.User;
import com.mo.core.enums.ProductApprovalStatus;
import com.mo.core.model.products.AbstractProduct;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "organisation_product_meta")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrganisationProductMeta {

    @EmbeddedId
    private OrganisationProductMetaId id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("organisationId")
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private AbstractProduct product;

    // Score assigned by organisation (immutable once set at product level)
    private Integer orgScore;

    // Cached customer average score for the product
    @Column(precision = 3, scale = 2)
    private BigDecimal customerAverageScore;

    private Integer customerReviewCount;

    // Commission percentage the organisation applies on this product (optional)
    @Column(precision = 5, scale = 2)
    private BigDecimal commissionPercent;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private ProductApprovalStatus approvalStatus = ProductApprovalStatus.PENDING;

    @CreationTimestamp
    private LocalDateTime submittedAt;

    private LocalDateTime validatedAt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validated_by_user_id")
    private User validatedBy;

    @Column(columnDefinition = "TEXT")
    private String validationComments;

    // SLA tracking: time elapsed since submission (in minutes)
    private Long slaMinutesElapsed;

    // SLA exceeded flag (true if validatedAt - submittedAt > 1440 minutes / 24 hours)
    @Builder.Default
    private boolean slaExceeded = false;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

