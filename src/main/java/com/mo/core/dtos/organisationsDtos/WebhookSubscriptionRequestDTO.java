package com.mo.core.dtos.organisationsDtos;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Schema(name = "WebhookSubscriptionRequest", description = "Payload to create a webhook subscription for an organisation")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
/**
 * WebhookSubscriptionRequestDTO
 *
 * Purpose: DTO for setting up webhooks so the organisation receives real-time
 * notifications when events occur (e.g., product validated, escrow released).
 *
 * Important fields:
 * - `url` (String): HTTPS endpoint where events will be POSTed.
 * - `eventTypes` (String): comma-separated event names (e.g., PENDING,APPROVED).
 * - `secret` (String): HMAC key for verifying webhook authenticity.
 *
 * Frontend guidance:
 * - Provide a UI to register webhook endpoints.
 * - Show instructions for validating webhook signatures using the secret.
 * - Display recent webhook delivery status and logs.
 */
public class WebhookSubscriptionRequestDTO {

    @NotNull
    @Schema(description = "Target URL for the webhook", example = "https://example.com/webhook", requiredMode = Schema.RequiredMode.REQUIRED)
    private String url;

    @Schema(description = "Comma separated event types", example = "PENDING,APPROVED")
    private String eventTypes;

    @Schema(description = "Shared secret for HMAC signing", example = "super-secret")
    private String secret;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(String eventTypes) {
        this.eventTypes = eventTypes;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}