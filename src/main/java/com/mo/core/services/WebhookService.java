package com.mo.core.services;

import com.mo.core.events.OrganisationProductValidationEvent;
import com.mo.core.model.organisations.WebhookSubscription;
import com.mo.core.repositories.jpa.OrganisationRepository;
import com.mo.core.repositories.jpa.WebhookSubscriptionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookService {

    private final WebhookSubscriptionRepository webhookRepo;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final OrganisationRepository organisationRepository;

    /**
     * Dispatch an event to all active subscriptions for the organisation that match the event type.
     */
    public void dispatch(OrganisationProductValidationEvent event) {
        try {
            List<WebhookSubscription> subs = webhookRepo.findByOrganisationIdAndActiveTrue(event.getOrganisationId());
            if (subs.isEmpty()) return;

            String payload = objectMapper.writeValueAsString(event);

            for (WebhookSubscription sub : subs) {
                if (!sub.isActive()) continue;
                String types = sub.getEventTypes();
                if (types != null && !types.isEmpty()) {
                    String[] arr = types.split(",");
                    boolean ok = false;
                    for (String t : arr) if (t.trim().equalsIgnoreCase(event.getEventType())) ok = true;
                    if (!ok) continue;
                }

                sendWithRetries(sub.getUrl(), payload, sub.getSecret());
            }
        } catch (Exception e) {
            log.error("Error dispatching webhook events", e);
        }
    }

    private void sendWithRetries(String url, String payload, String secret) {
        int maxAttempts = 3;
        Duration backoff = Duration.ofSeconds(2);
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                if (secret != null && !secret.isBlank()) {
                    String signature = computeHmacSha256(secret, payload);
                    headers.set("X-Hub-Signature-256", "sha256=" + signature);
                }
                HttpEntity<String> entity = new HttpEntity<>(payload, headers);
                restTemplate.postForEntity(url, entity, String.class);
                log.info("Webhook sent to {} (attempt={})", url, attempt);
                return;
            } catch (RestClientException ex) {
                log.warn("Webhook send failed to {} (attempt={}): {}", url, attempt, ex.getMessage());
                try { Thread.sleep(backoff.toMillis()); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            }
        }
        log.error("Webhook failed repeatedly for url={} after {} attempts", url, maxAttempts);
    }

    private String computeHmacSha256(String secret, String payload) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(keySpec);
            byte[] raw = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(raw.length * 2);
            for (byte b : raw) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to compute HMAC", e);
        }
    }

    public boolean verifySignature(String payload, String signatureHeader, String secret) {
        if (signatureHeader == null || secret == null || secret.isBlank()) {
            return false;
        }
        String expected = computeHmacSha256(secret, payload);
        String actual = signatureHeader.startsWith("sha256=") ? signatureHeader.substring(7) : signatureHeader;
        return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), actual.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Create a new webhook subscription for an organisation.
     */
    public WebhookSubscription createSubscription(Long organisationId, String url, String eventTypes, String secret) {
        var org = organisationRepository.findById(organisationId)
            .orElseThrow(() -> new IllegalArgumentException("Organisation not found"));
        WebhookSubscription sub = WebhookSubscription.builder()
            .organisation(org)
            .url(url)
            .eventTypes(eventTypes)
            .secret(secret)
            .active(true)
            .build();
        return webhookRepo.save(sub);
    }

    /**
     * List active subscriptions for an organisation.
     */
    public List<WebhookSubscription> listSubscriptions(Long organisationId) {
        return webhookRepo.findByOrganisationIdAndActiveTrue(organisationId);
    }

    /**
     * Deactivate a subscription (soft-delete).
     */
    public void deactivateSubscription(Long organisationId, Long subscriptionId) {
        WebhookSubscription sub = webhookRepo.findById(subscriptionId)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));
        if (sub.getOrganisation() == null || !sub.getOrganisation().getId().equals(organisationId)) {
            throw new IllegalArgumentException("Subscription does not belong to organisation");
        }
        sub.setActive(false);
        webhookRepo.save(sub);
    }
}
