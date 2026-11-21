package com.mo.core.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;

public record CreateNeedOfferRequest(
        @NotNull @JsonProperty("product_id") Long productId,
        @NotNull @JsonProperty("organisation_id") Long organisationId,
        String message
) {}
