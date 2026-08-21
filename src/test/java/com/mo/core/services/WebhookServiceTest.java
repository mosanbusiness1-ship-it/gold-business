package com.mo.core.services;

import com.mo.core.model.organisations.WebhookSubscription;
import com.mo.core.repositories.jpa.OrganisationRepository;
import com.mo.core.repositories.jpa.WebhookSubscriptionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WebhookServiceTest {

    private WebhookService webhookService;
    private WebhookSubscriptionRepository webhookRepo;
    private OrganisationRepository organisationRepository;

    @BeforeEach
    public void setup() {
        webhookRepo = mock(WebhookSubscriptionRepository.class);
        organisationRepository = mock(OrganisationRepository.class);
        webhookService = new WebhookService(webhookRepo, mock(RestTemplate.class), new ObjectMapper(), organisationRepository);
    }

    @Test
    public void verifySignature_shouldReturnTrue_forMatchingSignature() throws Exception {
        String payload = "{\"foo\":\"bar\"}";
        String secret = "my-secret";
        String signature = computeHmacSha256(secret, payload);

        assertThat(webhookService.verifySignature(payload, "sha256=" + signature, secret)).isTrue();
    }

    @Test
    public void verifySignature_shouldReturnFalse_forBadSignature() {
        String payload = "{\"foo\":\"bar\"}";
        String secret = "my-secret";

        assertThat(webhookService.verifySignature(payload, "sha256=bad-signature", secret)).isFalse();
    }

    @Test
    public void createSubscription_shouldSaveSecret() {
        when(organisationRepository.findById(1L)).thenReturn(Optional.of(mock(com.mo.core.model.organisations.Organisation.class)));
        when(webhookRepo.save(any(WebhookSubscription.class))).thenAnswer(inv -> inv.getArgument(0));

        WebhookSubscription sub = webhookService.createSubscription(1L, "https://example.com/webhook", "PENDING", "secret-token");

        assertThat(sub).isNotNull();
        assertThat(sub.getUrl()).isEqualTo("https://example.com/webhook");
        assertThat(sub.getSecret()).isEqualTo("secret-token");
    }

    private String computeHmacSha256(String secret, String payload) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(keySpec);
        byte[] raw = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder(raw.length * 2);
        for (byte b : raw) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}
