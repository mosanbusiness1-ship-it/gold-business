package com.mo.core.services;

import com.mo.core.events.PaymentRequestEvent;
import com.mo.core.kafka.PaymentProducer;
import com.mo.core.model.organisations.Organisation;
import com.mo.core.model.organisations.EscrowTransaction;
import com.mo.core.repositories.jpa.OrganisationRepository;
import com.mo.core.repositories.jpa.CommissionTransactionRepository;
import com.mo.core.repositories.jpa.EscrowTransactionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OrganisationServiceEscrowTest {

    private OrganisationRepository organisationRepository;
    private EscrowTransactionRepository escrowTransactionRepository;
    private CommissionTransactionRepository commissionTransactionRepository;
    private PaymentProducer paymentProducer;
    private OrganisationService organisationService;

    @BeforeEach
    public void setup() {
        organisationRepository = mock(OrganisationRepository.class);
        escrowTransactionRepository = mock(EscrowTransactionRepository.class);
        commissionTransactionRepository = mock(CommissionTransactionRepository.class);
        paymentProducer = mock(PaymentProducer.class);

        // Create stubs for other constructor params (not used by escrow tests)
        organisationService = new OrganisationService(
            organisationRepository,
            mock(com.mo.repositories.UserRepository.class),
            mock(com.mo.core.repositories.jpa.ProductRepository.class),
            mock(com.mo.mappers.organisationMappers.CreateOrganisationMapper.class),
            mock(com.mo.mappers.productsMappers.ElectronicProductMapper.class),
            mock(com.mo.mappers.productsMappers.FashionProductMapper.class),
            mock(com.mo.mappers.productsMappers.VehicleProductMapper.class),
            mock(com.mo.mappers.productsMappers.FoodProductMapper.class),
            mock(com.mo.mappers.productsMappers.RealEstateProductMapper.class),
            mock(com.mo.mappers.productsMappers.ServiceProductMapper.class),
            mock(com.mo.auth.JwtService.class),
            mock(com.mo.core.repositories.jpa.OrganisationMemberRepository.class),
            mock(com.mo.core.repositories.jpa.OrganisationProductReviewRepository.class),
            mock(com.mo.core.repositories.jpa.OrganisationProductMetaRepository.class),
            mock(com.mo.core.repositories.jpa.OrganisationRatingSummaryRepository.class),
            mock(com.mo.core.repositories.jpa.OrganisationReviewRepository.class),
            mock(com.mo.core.repositories.jpa.GuaranteePolicyRepository.class),
            commissionTransactionRepository,
            mock(com.mo.core.kafka.OrganisationValidationProducer.class),
            paymentProducer,
            mock(com.mo.core.repositories.jpa.GuaranteeClaimRepository.class),
            escrowTransactionRepository
        );
    }

    @Test
    public void createEscrow_shouldSaveEscrowWithHeldStatus() {
        Organisation org = Organisation.builder().id(42L).build();
        when(organisationRepository.findById(42L)).thenReturn(Optional.of(org));

        ArgumentCaptor<EscrowTransaction> captor = ArgumentCaptor.forClass(EscrowTransaction.class);
        when(escrowTransactionRepository.save(any(EscrowTransaction.class)))
            .thenAnswer(inv -> {
                EscrowTransaction e = inv.getArgument(0);
                e.setId(100L);
                return e;
            });

        EscrowTransaction result = organisationService.createEscrow(42L, 7L, new BigDecimal("123.45"), "meta-json");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getOrganisation().getId()).isEqualTo(42L);
        assertThat(result.getProductId()).isEqualTo(7L);
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("123.45"));
        assertThat(result.getStatus()).isEqualTo(com.mo.core.enums.CommissionTransactionStatus.HELD);
        verify(paymentProducer).emitPaymentRequest(argThat(event ->
            event != null && "HOLD".equals(event.getAction()) &&
            "escrow-100".equals(event.getTransactionRef()) &&
            event.getAmount().compareTo(new BigDecimal("123.45")) == 0
        ));
    }

    @Test
    public void releaseEscrow_shouldMarkReleasedAndCreateCommissionTransaction() {
        Organisation org = Organisation.builder().id(2L).build();
        EscrowTransaction escrow = EscrowTransaction.builder()
            .id(200L)
            .organisation(org)
            .productId(5L)
            .amount(new BigDecimal("50.00"))
            .status(com.mo.core.enums.CommissionTransactionStatus.HELD)
            .build();

        when(escrowTransactionRepository.findById(200L)).thenReturn(Optional.of(escrow));
        when(escrowTransactionRepository.save(any(EscrowTransaction.class))).thenAnswer(i -> i.getArgument(0));
        when(commissionTransactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        EscrowTransaction released = organisationService.releaseEscrow(200L, 900L);

        assertThat(released.getStatus()).isEqualTo(com.mo.core.enums.CommissionTransactionStatus.RELEASED);
        assertThat(released.getReleasedAt()).isNotNull();
        verify(commissionTransactionRepository).save(any());
        verify(paymentProducer).emitPaymentRequest(argThat(event ->
            event != null && "RELEASE".equals(event.getAction()) &&
            "escrow-200".equals(event.getTransactionRef()) &&
            event.getAmount().compareTo(new BigDecimal("50.00")) == 0
        ));
    }

    @Test
    public void refundEscrow_shouldMarkRefundedAndRecordRefundTransaction() {
        Organisation org = Organisation.builder().id(3L).build();
        EscrowTransaction escrow = EscrowTransaction.builder()
            .id(300L)
            .organisation(org)
            .productId(9L)
            .amount(new BigDecimal("10.00"))
            .status(com.mo.core.enums.CommissionTransactionStatus.HELD)
            .metadata(null)
            .build();

        when(escrowTransactionRepository.findById(300L)).thenReturn(Optional.of(escrow));
        when(escrowTransactionRepository.save(any(EscrowTransaction.class))).thenAnswer(i -> i.getArgument(0));
        when(commissionTransactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        EscrowTransaction refunded = organisationService.refundEscrow(300L, "buyer-cancelled");

        assertThat(refunded.getStatus()).isEqualTo(com.mo.core.enums.CommissionTransactionStatus.REFUNDED);
        assertThat(refunded.getMetadata()).contains("refundReason:buyer-cancelled");
        verify(commissionTransactionRepository).save(any());
        verify(paymentProducer).emitPaymentRequest(argThat(event ->
            event != null && "REFUND".equals(event.getAction()) &&
            "refund-for-escrow-300".equals(event.getTransactionRef()) &&
            event.getAmount().compareTo(new BigDecimal("10.00")) == 0
        ));
    }

    @Test
    public void resolveGuaranteeClaim_shouldRefundHeldEscrowAndMarkClaimResolved() {
        Organisation org = Organisation.builder().id(7L).build();
        com.mo.core.model.organisations.GuaranteeClaim claim = com.mo.core.model.organisations.GuaranteeClaim.builder()
            .id(42L)
            .organisation(org)
            .productId(13L)
            .reason("faulty product")
            .resolved(false)
            .build();

        EscrowTransaction heldEscrow = EscrowTransaction.builder()
            .id(500L)
            .organisation(org)
            .productId(13L)
            .amount(new BigDecimal("99.99"))
            .status(com.mo.core.enums.CommissionTransactionStatus.HELD)
            .build();

        when(commissionTransactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(escrowTransactionRepository.findById(500L)).thenReturn(Optional.of(heldEscrow));
        when(escrowTransactionRepository.findByOrganisationId(7L)).thenReturn(java.util.List.of(heldEscrow));
        when(escrowTransactionRepository.save(any(EscrowTransaction.class))).thenAnswer(i -> i.getArgument(0));

        com.mo.core.repositories.jpa.GuaranteeClaimRepository guaranteeClaimRepository = mock(com.mo.core.repositories.jpa.GuaranteeClaimRepository.class);
        when(guaranteeClaimRepository.findById(42L)).thenReturn(Optional.of(claim));
        when(guaranteeClaimRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        organisationService = new OrganisationService(
            organisationRepository,
            mock(com.mo.repositories.UserRepository.class),
            mock(com.mo.core.repositories.jpa.ProductRepository.class),
            mock(com.mo.mappers.organisationMappers.CreateOrganisationMapper.class),
            mock(com.mo.mappers.productsMappers.ElectronicProductMapper.class),
            mock(com.mo.mappers.productsMappers.FashionProductMapper.class),
            mock(com.mo.mappers.productsMappers.VehicleProductMapper.class),
            mock(com.mo.mappers.productsMappers.FoodProductMapper.class),
            mock(com.mo.mappers.productsMappers.RealEstateProductMapper.class),
            mock(com.mo.mappers.productsMappers.ServiceProductMapper.class),
            mock(com.mo.auth.JwtService.class),
            mock(com.mo.core.repositories.jpa.OrganisationMemberRepository.class),
            mock(com.mo.core.repositories.jpa.OrganisationProductReviewRepository.class),
            mock(com.mo.core.repositories.jpa.OrganisationProductMetaRepository.class),
            mock(com.mo.core.repositories.jpa.OrganisationRatingSummaryRepository.class),
            mock(com.mo.core.repositories.jpa.OrganisationReviewRepository.class),
            mock(com.mo.core.repositories.jpa.GuaranteePolicyRepository.class),
            commissionTransactionRepository,
            mock(com.mo.core.kafka.OrganisationValidationProducer.class),
            paymentProducer,
            guaranteeClaimRepository,
            escrowTransactionRepository
        );

        com.mo.core.model.organisations.GuaranteeClaim resolved = organisationService.resolveGuaranteeClaim(42L, "approved refund", 101L);

        assertThat(resolved.isResolved()).isTrue();
        assertThat(resolved.getResolutionNotes()).contains("approved refund");
        verify(commissionTransactionRepository).save(any());
        verify(paymentProducer).emitPaymentRequest(any(PaymentRequestEvent.class));
    }
}
