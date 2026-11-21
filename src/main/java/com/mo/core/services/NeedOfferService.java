package com.mo.core.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mo.auth.User;
import com.mo.core.model.needs.AbstractUserNeed;
import com.mo.core.model.needs.NeedOffer;
import com.mo.core.model.organisations.Organisation;
import com.mo.core.model.products.AbstractProduct;
import com.mo.core.repositories.jpa.NeedOfferRepository;
import com.mo.core.repositories.jpa.OrganisationRepository;
import com.mo.core.repositories.jpa.ProductRepository;
import com.mo.core.repositories.jpa.UserNeedRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NeedOfferService {

    private final NeedOfferRepository needOfferRepository;
    private final UserNeedRepository userNeedRepository;
    private final ProductRepository productRepository;
    private final OrganisationRepository organisationRepository;

    @Transactional
    public NeedOffer createOffer(Long needId, Long productId, Long organisationId, User offerer, String message) {
        AbstractUserNeed need = userNeedRepository.findById(needId)
                .orElseThrow(() -> new IllegalArgumentException("Need introuvable: " + needId));

        AbstractProduct product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produit introuvable: " + productId));

        Organisation organisation = organisationRepository.findById(organisationId)
                .orElseThrow(() -> new IllegalArgumentException("Organisation introuvable: " + organisationId));

        if (need.getOrganisations() == null || need.getOrganisations().stream()
                .noneMatch(org -> org.getId().equals(organisationId))) {
            throw new IllegalArgumentException("Le need n'est pas lié à cette organisation");
        }

        if (organisation.getProducts() == null || organisation.getProducts().stream()
                .noneMatch(p -> p.getId().equals(productId))) {
            throw new IllegalArgumentException("Le produit n'appartient pas à cette organisation");
        }

        NeedOffer offer = NeedOffer.builder()
                .need(need)
                .product(product)
                .offerer(offerer)
                .organisation(organisation)
                .message(message)
                .build();

        return needOfferRepository.save(offer);
    }

    public List<NeedOffer> getOffersForNeed(Long needId) {
        return needOfferRepository.findByNeedId(needId);
    }
}
