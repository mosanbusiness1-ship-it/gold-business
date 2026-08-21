package com.mo.core.model.organisations;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.mo.auth.User;
import com.mo.core.enums.OrganisationReviewStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "organisation_review")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrganisationReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User reviewer;

    @Column(nullable = false)
    private Integer rating; // 1-5

    private String title;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @JsonIgnore
    @JoinColumn(name = "order_id")
    private Long orderId;

    @Builder.Default
    private boolean isVerifiedPurchase = false;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private OrganisationReviewStatus status = OrganisationReviewStatus.PUBLISHED;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
