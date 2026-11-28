package com.mo.core.dtos;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.model.needs.NeedOffer;

public record NeedOfferResponse(
        Long id,
        @JsonProperty("need_id") Long needId,
        @JsonProperty("product_id") Long productId,
        @JsonProperty("organisation_id") Long organisationId,
        @JsonProperty("offerer") OffererResponse offerer,
        String message,
        @JsonProperty("created_at") LocalDateTime createdAt
) {
    public record OffererResponse(
            Long id,
            @JsonProperty("full_name") String fullName,
            String email
    ) {}

    public static NeedOfferResponse from(NeedOffer offer) {
        return new NeedOfferResponse(
                offer.getId(),
                offer.getNeed().getId(),
                offer.getProduct().getId(),
                offer.getOrganisation().getId(),
                new OffererResponse(
                        offer.getOfferer().getId(),
                        offer.getOfferer().getFullName(),
                        offer.getOfferer().getEmail()),
                offer.getMessage(),
                offer.getCreatedAt()
        );
    }
}
