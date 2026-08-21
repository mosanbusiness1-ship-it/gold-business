package com.mo.core.eventsconsumers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mo.core.dtos.NotificationData;
import com.mo.core.events.AuctionEndedEvent;
import com.mo.core.model.auctions.Auction;
import com.mo.core.model.products.AbstractProduct;
import com.mo.core.services.AuctionService;
import com.mo.core.services.BidService;
import com.mo.core.services.audit.AuditLogService;
import com.mo.core.visitors.product_visitors.ProductVisitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Listener critique pour la fin des enchères
 * Responsable de:
 * - Calcul du produit gagnant
 * - Mise à jour du statut des bids
 * - Notification de l'utilisateur
 * - Audit trail complet avec persistence
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuctionEndedListener {

    private final ProductVisitor<Double> qualityEvaluationVisitor;
    private final AuctionService auctionService;
    private final BidService bidService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final AuditLogService auditLogService;

    @EventListener
    public void handleAuctionEnded(AuctionEndedEvent event) {
        Auction auction = event.getAuction();
        Long auctionId = auction.getId();
        Long userId = auction.getNeed() != null ? auction.getNeed().getUser().getId() : null;

        log.info("[AUCTION_ENDED] Handling ended auction id={}", auctionId);

        try {
            // 1. Enregistrement d'audit initial
            var auditLog = auditLogService.logEventSuccess(
                    "AUCTION_ENDED",
                    String.format("Auction %d processing started", auctionId),
                    event,
                    userId
            );
            Long auditLogId = auditLog.getId();

            List<AbstractProduct> products = auctionService.getProducts(auctionId);
            BigDecimal maxPrice = auction.getNeed() != null ? auction.getNeed().getMaxPrice() : BigDecimal.ONE;
            double alpha = 0.5; // pondération qualité/prix

            // 2. Calcul du produit gagnant
            AbstractProduct winningProduct = products.stream()
                    .max(Comparator.comparingDouble(p -> {
                        double quality = p.accept(qualityEvaluationVisitor);
                        double priceScore = 1 - (p.getPrice().doubleValue() / (maxPrice.doubleValue() == 0 ? 1 : maxPrice.doubleValue()));
                        return alpha * priceScore + (1 - alpha) * quality;
                    }))
                    .orElse(null);

            if (winningProduct != null) {
                log.info("[AUCTION_ENDED] ✅ Winning product for auction {}: {} (price: {}, quality leverage)", 
                        auctionId, winningProduct.getName(), winningProduct.getPrice());

                // 3. Marquer les bids comme gagnants/perdants
                try {
                    bidService.markBidStatus(auctionId, winningProduct.getId());
                    log.debug("[AUCTION_ENDED] Bid status updated for auction {} with winning product {}", 
                            auctionId, winningProduct.getId());
                } catch (Exception e) {
                    log.error("[AUCTION_ENDED] ❌ Failed to update bid status for auction {}", auctionId, e);
                    auditLogService.markAsKafkaFailed(auditLogId, "auction-ended-process", 
                            "Bid status update failed: " + e.getMessage());
                }

                // 4. Publier notification Kafka
                try {
                    NotificationData notification = prepareNotification(auction, winningProduct);
                    String payload = objectMapper.writeValueAsString(notification);
                    kafkaTemplate.send("notify-user-for-winningAuction", payload);
                    
                    log.debug("[AUCTION_ENDED] Published winning auction notification for auction {}", auctionId);
                } catch (Exception e) {
                    log.error("[AUCTION_ENDED] ❌ Failed to publish auction winning notification", e);
                    auditLogService.markAsKafkaFailed(auditLogId, "notify-user-for-winningAuction", 
                            "Notification publish failed: " + e.getMessage());
                }
            } else {
                log.warn("[AUCTION_ENDED] ⚠️ No winning product found for auction {}", auctionId);
                auditLogService.markAsKafkaFailed(auditLogId, "auction-ended-process", "No winning product found");
            }

            // 5. Marquer l'enchère comme terminée et persister
            try {
                auction.setActived(false);
                auctionService.save(auction);
                auditLogService.markAsPublished(auditLogId, "auction-ended-process");
                log.info("[AUCTION_ENDED] ✅ Auction {} completed successfully and saved", auctionId);
            } catch (Exception e) {
                log.error("[AUCTION_ENDED] ❌ Failed to persist auction {} status", auctionId, e);
                auditLogService.markAsKafkaFailed(auditLogId, "auction-persistence", 
                        "Failed to persist auction: " + e.getMessage());
            }

        } catch (Exception e) {
            log.error("[AUCTION_ENDED] ❌ CRITICAL ERROR in auction processing for auctionId={}", auctionId, e);
            try {
                auditLogService.logEventFailure(
                        "AUCTION_ENDED",
                        String.format("Critical error processing auction %d", auctionId),
                        event,
                        userId,
                        e
                );
            } catch (Exception auditError) {
                log.error("[AUCTION_ENDED] ❌ Failed to log critical error to audit", auditError);
            }
        }
    }

    /**
     * Préparer les données de notification pour l'utilisateur
     */
    private NotificationData prepareNotification(Auction auction, AbstractProduct winningProduct) {
        NotificationData notification = new NotificationData();
        
        if (auction.getNeed() != null && auction.getNeed().getUser() != null) {
            var u = auction.getNeed().getUser();
            notification.setUserName(u.getUsername());
            notification.setUserFullName(u.getFullName());
            notification.setUserPhoneNumber(u.getPhoneNumber());
            notification.setUserEmail(u.getEmail());
        }

        notification.setProductAmount(winningProduct.getPrice());
        notification.setProductCurrency(winningProduct.getCurrency());
        notification.setProductName(winningProduct.getName());
        notification.setProductQuantity(1);
        notification.setNeedDescription(auction.getNeed() != null ? auction.getNeed().getDescription() : null);
        notification.setEventType("auction_won");
        notification.setAuctionId(auction.getId());
        notification.setNeedId(auction.getNeed() != null ? auction.getNeed().getId() : null);
        notification.setProductId(winningProduct.getId());
        notification.setEventTimestamp(LocalDateTime.now().toString());

        return notification;
    }
}

