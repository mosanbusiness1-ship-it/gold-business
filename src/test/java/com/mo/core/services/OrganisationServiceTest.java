package com.mo.core.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.mo.auth.User;
import com.mo.core.model.organisations.Organisation;
import com.mo.core.model.organisations.OrganisationProductMeta;
import com.mo.core.model.organisations.OrganisationProductReview;
import com.mo.core.model.organisations.OrganisationProductReviewId;
import com.mo.core.model.products.AbstractProduct;
import com.mo.core.model.products.VehicleProduct;
import com.mo.core.repositories.jpa.OrganisationProductReviewRepository;
import com.mo.core.repositories.jpa.OrganisationMemberRepository;
import com.mo.core.repositories.jpa.OrganisationProductMetaRepository;
import com.mo.core.repositories.jpa.OrganisationRatingSummaryRepository;
import com.mo.core.repositories.jpa.OrganisationRepository;
import com.mo.core.repositories.jpa.ProductRepository;
import com.mo.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrganisationServiceTest {

    @Mock
    OrganisationRepository organisationRepository;

    @Mock
    OrganisationMemberRepository organisationMemberRepository;

    @Mock
    OrganisationProductReviewRepository productReviewRepository;

    @Mock
    OrganisationProductMetaRepository productMetaRepository;

    @Mock
    OrganisationRatingSummaryRepository ratingSummaryRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    OrganisationService organisationService;

    private Organisation org;
    private AbstractProduct product;
    private User moderator;

    @BeforeEach
    void setUp() {
        org = new Organisation();
        org.setId(10L);

        product = new VehicleProduct();
        product.setId(222L);

        org.getProducts().add(product);

        moderator = new User();
        moderator.setId(99L);
        moderator.setEmail("mod@example.com");
    }

    @Test
    void assignOrganisationProductScore_shouldSaveReviewAndMeta() {
        when(organisationRepository.findById(10L)).thenReturn(Optional.of(org));
        when(productRepository.findById(222L)).thenReturn(Optional.of(product));
        when(productReviewRepository.findByIdOrganisationIdAndIdProductId(10L, 222L)).thenReturn(Optional.empty());
        when(userRepository.findById(99L)).thenReturn(Optional.of(moderator));
        when(productMetaRepository.findByIdOrganisationIdAndIdProductId(10L, 222L)).thenReturn(Optional.empty());

        OrganisationProductReview saved = new OrganisationProductReview();
        saved.setId(new OrganisationProductReviewId(10L, 222L));
        when(productReviewRepository.save(any())).thenReturn(saved);

        var review = organisationService.assignOrganisationProductScore(10L, 222L, 4, "ok", 99L);

        assertNotNull(review);
        assertEquals(10L, review.getId().getOrganisationId());
        assertEquals(222L, review.getId().getProductId());

        ArgumentCaptor<OrganisationProductMeta> metaCaptor = ArgumentCaptor.forClass(OrganisationProductMeta.class);
        verify(productMetaRepository).save(metaCaptor.capture());
        OrganisationProductMeta meta = metaCaptor.getValue();
        assertEquals(4, meta.getOrgScore());
    }
}
