package com.mo.api.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mo.core.services.OrganisationService;
import com.mo.core.services.ProductService;
import com.mo.core.services.UserNeedService;
import com.mo.core.services.OrganisationMembershipService;
import com.mo.core.services.WebhookService;
import com.mo.core.visitors.need_visitors.UserNeedVisitorRegistry;
import com.mo.mappers.organisationMappers.CreateOrganisationResponseMapper;
import com.mo.mappers.organisationMappers.OrgProductMapper;
import com.mo.mappers.productsMappers.ProductMapperJackson;
import com.mo.mappers.needMappers.NeedMapperJackson;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mo.auth.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = OrganisationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class OrganisationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrganisationService organisationService;
    
    @MockBean
    private UserNeedVisitorRegistry visitorRegistry;
    
    @MockBean
    private ProductService productService;

    @MockBean
    private com.mo.configuration.OrganisationSecurity organisationSecurity;
    
    @MockBean
    private UserNeedService userNeedService;
    
    @MockBean
    private OrganisationMembershipService organisationMembershipService;
    
    @MockBean
    private WebhookService webhookService;
    
    @MockBean
    private CreateOrganisationResponseMapper createOrganisationResponseMapper;
    
    @MockBean
    private OrgProductMapper orgProductMapper;
    
    @MockBean
    private ProductMapperJackson mapperVisitor;
    
    @MockBean
    private NeedMapperJackson needmapper;
    
    @MockBean
    private JwtService jwtService;

    @MockBean
    private com.mo.auth.TokenBlacklistService tokenBlacklistService;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @BeforeEach
    public void setup() {
    }

    @Test
    public void validateProductEndpoint_shouldReturnOk_whenServiceSucceeds() throws Exception {
        Long orgId = 1L;
        Long productId = 2L;

        // service is mocked to do nothing (void)
        org.mockito.Mockito.doNothing().when(organisationService).validateProduct(orgId, productId, true, "ok", 10L);

        String body = "{\"approved\":true,\"comments\":\"ok\"}";

        mockMvc.perform(post("/api/organisations/1/validateProduct/2")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", 10L)
                .content(body))
                .andExpect(status().isOk());
    }

    @Test
    public void getReviewsEndpoint_shouldReturnOk() throws Exception {
        com.mo.core.dtos.organisationsDtos.OrganisationReviewDTO dto = com.mo.core.dtos.organisationsDtos.OrganisationReviewDTO.builder()
            .id(11L)
            .rating(4)
            .title("Nice")
            .comment("Good service")
            .isVerifiedPurchase(true)
            .status("PUBLISHED")
            .build();

        var page = new PageImpl<>(java.util.List.of(dto), PageRequest.of(0, 5), 1);
        org.mockito.Mockito.when(organisationService.getOrganisationReviewDtos(1L, false, 0, 5)).thenReturn(page);

        mockMvc.perform(get("/api/organisations/1/reviews?page=0&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

        @Test
        public void submitProductForValidation_shouldReturnCreated() throws Exception {
        // Mock service to return a simple OrganisationProductMeta stub
        com.mo.core.model.organisations.OrganisationProductMeta meta = com.mo.core.model.organisations.OrganisationProductMeta.builder()
            .id(new com.mo.core.model.organisations.OrganisationProductMetaId(1L, 2L))
            .approvalStatus(com.mo.core.enums.ProductApprovalStatus.PENDING)
            .build();

        org.mockito.Mockito.when(organisationService.submitProductForValidation(1L, 2L)).thenReturn(meta);

        mockMvc.perform(post("/api/organisations/1/products/2/submit"))
            .andExpect(status().isCreated());
        }

        @Test
        public void validateProductEndpoint_reject_shouldReturnOk() throws Exception {
        Long orgId = 1L;
        Long productId = 3L;

        org.mockito.Mockito.doNothing().when(organisationService).validateProduct(orgId, productId, false, "invalid", 10L);

        String body = "{\"approved\":false,\"comments\":\"invalid\"}";

        mockMvc.perform(post("/api/organisations/1/validateProduct/3")
            .contentType(MediaType.APPLICATION_JSON)
            .requestAttr("userId", 10L)
            .content(body))
            .andExpect(status().isOk());
        }

        @Test
        public void getReviewsEndpoint_pagination_shouldReturnContent() throws Exception {
        // Prepare DTO content
        com.mo.core.dtos.organisationsDtos.OrganisationReviewDTO dto = com.mo.core.dtos.organisationsDtos.OrganisationReviewDTO.builder()
            .id(11L)
            .rating(4)
            .title("Nice")
            .comment("Good service")
            .isVerifiedPurchase(true)
            .status("PUBLISHED")
            .build();

        var page = new PageImpl<>(java.util.List.of(dto), PageRequest.of(0, 5), 1);

        org.mockito.Mockito.when(organisationService.getOrganisationReviewDtos(1L, false, 0, 5)).thenReturn(page);

        mockMvc.perform(get("/api/organisations/1/reviews?page=0&size=5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].rating").value(4));
        }

    @Test
    public void createWebhookSubscription_shouldReturnCreated() throws Exception {
        String body = "{\"url\":\"https://example.com/webhook\",\"eventTypes\":\"PENDING,APPROVED\",\"secret\":\"super-secret\"}";
        com.mo.core.model.organisations.WebhookSubscription sub = com.mo.core.model.organisations.WebhookSubscription.builder()
            .id(11L)
            .url("https://example.com/webhook")
            .eventTypes("PENDING,APPROVED")
            .active(true)
            .build();
        org.mockito.Mockito.when(webhookService.createSubscription(1L, "https://example.com/webhook", "PENDING,APPROVED", "super-secret"))
            .thenReturn(sub);

        mockMvc.perform(post("/api/organisations/1/webhooks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
            .andExpect(status().isCreated());
    }

    @Test
    public void listWebhookSubscriptions_shouldReturnOk() throws Exception {
        com.mo.core.model.organisations.WebhookSubscription sub = com.mo.core.model.organisations.WebhookSubscription.builder()
            .id(12L)
            .url("https://example.com/webhook")
            .eventTypes("PENDING")
            .active(true)
            .build();
        org.mockito.Mockito.when(webhookService.listSubscriptions(1L)).thenReturn(java.util.List.of(sub));

        mockMvc.perform(get("/api/organisations/1/webhooks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(12));
    }

    @Test
    public void deleteWebhookSubscription_shouldReturnNoContent() throws Exception {
        org.mockito.Mockito.doNothing().when(webhookService).deactivateSubscription(1L, 99L);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/organisations/1/webhooks/99"))
            .andExpect(status().isNoContent());
    }

    @Test
    public void createGuaranteeClaim_shouldReturnCreated() throws Exception {
        String body = "{\"productId\":2,\"reason\":\"faulty product\"}";
        com.mo.core.model.organisations.GuaranteeClaim claim = com.mo.core.model.organisations.GuaranteeClaim.builder()
            .id(21L)
            .productId(2L)
            .reason("faulty product")
            .resolved(false)
            .build();

        org.mockito.Mockito.when(organisationService.createGuaranteeClaim(1L, 2L, "faulty product"))
            .thenReturn(claim);

        mockMvc.perform(post("/api/organisations/1/guarantee/claims")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(21));
    }

    @Test
    public void resolveGuaranteeClaim_shouldReturnOk() throws Exception {
        String body = "{\"notes\":\"approved refund\",\"resolverId\":99}";
        com.mo.core.model.organisations.GuaranteeClaim claim = com.mo.core.model.organisations.GuaranteeClaim.builder()
            .id(22L)
            .productId(3L)
            .reason("broken")
            .resolved(true)
            .resolutionNotes("approved refund")
            .build();

        org.mockito.Mockito.when(organisationService.resolveGuaranteeClaim(22L, "approved refund", 99L))
            .thenReturn(claim);

        mockMvc.perform(post("/api/organisations/1/guarantee/claims/22/resolve")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resolved").value(true));
    }
}

