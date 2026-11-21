package com.mo.core.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mo.auth.User;
import com.mo.core.enums.TransactionStatus;
import com.mo.core.model.products.AbstractProduct;
import com.mo.core.model.transactions.ProductTransaction;
import com.mo.core.repositories.jpa.ProductRepository;
import com.mo.core.repositories.jpa.ProductTransactionRepository;
import com.mo.repositories.UserRepository;
import com.mo.core.repositories.jpa.OrganisationRepository;
import com.mo.core.model.organisations.Organisation;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductTransactionService {

    private final ProductTransactionRepository transactionRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrganisationRepository organisationRepository;

public ProductTransaction createTransaction(Long productId, Long buyerId, Long organisationId) {
    AbstractProduct product = productRepository.findById(productId)
        .orElseThrow(() -> new IllegalArgumentException("Produit introuvable"));

    if (!product.isEnabled()) {
        throw new IllegalStateException("Ce produit est actuellement indisponible.");
    }

    User buyer = userRepository.findById(buyerId)
        .orElseThrow(() -> new IllegalArgumentException("Acheteur introuvable"));

    User seller = product.getOwner(); // à condition d'avoir un champ `owner` dans AbstractProduct

    if (buyer.equals(seller)) {
        throw new IllegalArgumentException("Un utilisateur ne peut pas acheter son propre produit");
    }

    Organisation organisation = null;
    if (organisationId != null) {
        organisation = organisationRepository.findById(organisationId)
            .orElseThrow(() -> new IllegalArgumentException("Organisation introuvable"));

        if (organisation.getProducts() == null || organisation.getProducts().stream().noneMatch(p -> p.getId().equals(productId))) {
            throw new IllegalArgumentException("Le produit ne participe pas à cette organisation");
        }
    }

    ProductTransaction transaction = ProductTransaction.builder()
        .product(product)
        .buyer(buyer)
        .seller(seller)
        .organisation(organisation) // Associer l'organisation à la transaction
        .amount(product.getPrice())
        .status(TransactionStatus.PENDING)
        .build();

    return transactionRepository.save(transaction);
}


    public ProductTransaction completeTransaction(Long transactionId) {
        ProductTransaction tx = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new IllegalArgumentException("Transaction introuvable"));

        tx.setStatus(TransactionStatus.COMPLETED);
        return transactionRepository.save(tx);
    }

    public ProductTransaction cancelTransaction(Long transactionId) {
        ProductTransaction tx = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new IllegalArgumentException("Transaction introuvable"));

        tx.setStatus(TransactionStatus.CANCELLED);
        return transactionRepository.save(tx);
    }

    public List<ProductTransaction> getPurchasesByBuyer(Long buyerId) {
        return transactionRepository.findByBuyerId(buyerId);
    }

    public List<ProductTransaction> getSalesBySeller(Long sellerId) {
        return transactionRepository.findBySellerId(sellerId);
    }


}
