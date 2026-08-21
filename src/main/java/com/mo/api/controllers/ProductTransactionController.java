package com.mo.api.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import com.mo.auth.User;
import com.mo.core.model.transactions.ProductTransaction;
import com.mo.core.services.ProductTransactionService;
import com.mo.core.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class ProductTransactionController {

    private final ProductTransactionService transactionService;
    private final UserService userService;

    @PostMapping("/buy/{productId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Buy a product",
        description = "Create a purchase transaction for the currently authenticated buyer",
        responses = @ApiResponse(
            responseCode = "200",
            description = "Created purchase transaction",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductTransaction.class))
        )
    )
    public ResponseEntity<ProductTransaction> buyProduct(@PathVariable Long productId, Principal principal) {
        User buyer = userService.findByEmail(principal.getName());
        ProductTransaction tx = transactionService.createTransaction(productId, buyer.getId());
        return ResponseEntity.ok(tx);
    }

    @PostMapping("/{transactionId}/complete")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    @Operation(summary = "Complete transaction", description = "Mark the transaction as completed")
    public ResponseEntity<ProductTransaction> completeTransaction(@PathVariable Long transactionId) {
        ProductTransaction tx = transactionService.completeTransaction(transactionId);
        return ResponseEntity.ok(tx);
    }

    @PostMapping("/{transactionId}/cancel")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    @Operation(summary = "Cancel transaction", description = "Cancel a pending transaction")
    public ResponseEntity<ProductTransaction> cancelTransaction(@PathVariable Long transactionId) {
        ProductTransaction tx = transactionService.cancelTransaction(transactionId);
        return ResponseEntity.ok(tx);
    }

    @GetMapping("/buyer")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get my purchases", description = "Return purchase transactions for the authenticated buyer")
    public ResponseEntity<List<ProductTransaction>> getMyPurchases(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        return ResponseEntity.ok(transactionService.getPurchasesByBuyer(user.getId()));
    }

    @GetMapping("/seller")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get my sales", description = "Return sales transactions for the authenticated seller")
    public ResponseEntity<List<ProductTransaction>> getMySales(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        return ResponseEntity.ok(transactionService.getSalesBySeller(user.getId()));
    }
}

