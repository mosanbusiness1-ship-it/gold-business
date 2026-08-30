package com.mo.notification.consumers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mo.core.dtos.NotificationData;
import com.mo.core.dtos.ProductForUserNotification;
import com.mo.core.dtos.autoPurchase.AutoPurchaseNotificationDataDTO;
import com.mo.core.dtos.autoPurchase.AutoPurchaseResponse;
import com.mo.core.dtos.autoPurchase.ConfirmPendingTransferData;
import com.mo.notification.services.NotificationService;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "app.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class NotificationConsumers {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
        topics = {
            "notify-auto-purchase-completed",
            "notify-pending-transaction",
            "notify-user-for-strictMatches",
            "notify-user-for-similarMatches",
            "notify-user-for-winningAuction"
        },
        groupId = "notification-service",
        concurrency = "1" // garde un seul thread consumer pour éviter les rebalances inutiles
    )
    public void onMessage(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic, String data) {
        try {
            switch (topic) {
                case "notify-auto-purchase-completed" -> handleAutoPurchaseCompleted(data);
                case "notify-pending-transaction" -> handlePendingTransaction(data);
                case "notify-user-for-strictMatches" -> handleStrictMatches(data);
                case "notify-user-for-similarMatches" -> handleSimilarMatches(data);
                case "notify-user-for-winningAuction" -> handleWinningAuction(data);
                default -> log.warn("⚠️ Topic inattendu: {}", topic);
            }
        } catch (Exception e) {
            log.error("❌ Erreur traitement message du topic {}. Payload: {}", topic, data, e);
        }
    }

    private void handleAutoPurchaseCompleted(String data) throws Exception {
        AutoPurchaseResponse dto = objectMapper.readValue(data, AutoPurchaseResponse.class);
        log.info("📥 COMPLETED reçu: {}", dto);
        dto.setSuccess(true);
        notificationService.autoPurchaseNotifyAllChannels(dto);
    }

    private void handlePendingTransaction(String data) throws Exception {
        ConfirmPendingTransferData dto = objectMapper.readValue(data, ConfirmPendingTransferData.class);
        log.warn("📥 FAILED/PENDING reçu: {}", dto);
        notificationService.confirmPendingTransferNotifyAllChannels(dto);
    }

    private void handleStrictMatches(String data) throws Exception {
        NotificationData notification = objectMapper.readValue(data, NotificationData.class);
        log.info("📥 STRICT MATCH reçu: {}", notification);
        notificationService.notifyAllChannels(notification);
    }

    private void handleSimilarMatches(String data) throws Exception {
        NotificationData notification = objectMapper.readValue(data, NotificationData.class);
        log.info("📥 SIMILAR MATCH reçu: {}", notification);
        notificationService.notifyAllChannels(notification);
    }

    private void handleWinningAuction(String data) throws Exception {
        NotificationData notification = objectMapper.readValue(data, NotificationData.class);
        log.info("📥 AUCTION WIN reçu: {}", notification);
        notificationService.notifyAllChannels(notification);
    }
}