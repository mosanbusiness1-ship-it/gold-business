package com.mo.notification.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.mo.core.dtos.NotificationData;
import com.mo.core.dtos.autoPurchase.AutoPurchaseNotificationDataDTO;
import com.mo.core.dtos.autoPurchase.AutoPurchaseResponse;
import com.mo.core.dtos.autoPurchase.ConfirmPendingTransferData;
import com.mo.notification.events.NotificationEvent;
import com.mo.notification.factory.NotificationFactory;
import com.mo.notification.strategies.NotificationStrategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final ApplicationEventPublisher publisher;
    private final NotificationFactory notificationFactory;

    /* ===================== AUTO PURCHASE ===================== */
    
    public void confirmPendingTransferNotifyAllChannels(ConfirmPendingTransferData data) {
        if (!isConfirmPendingTransferDataValid(data)) {
            log.warn("⚠️ ConfirmPendingTransferData data invalide ou incomplète, diffusion ignorée : {}", data);
            return;
        }
        log.info("▶ [ConfirmPendingTransfer] Diffusion sur tous les canaux : {}", data);
        publisher.publishEvent(new NotificationEvent(this, data));
    }
    

    public void autoPurchaseNotifyOne(String channel, AutoPurchaseResponse data) {
        if (!isAutoPurchaseDataValid(data)) {
            log.warn("⚠️ AutoPurchase data invalide ou incomplète, notification ignorée : {}", data);
            return;
        }
        try {
            NotificationStrategy<AutoPurchaseResponse> strategy =
                    notificationFactory.getStrategy(channel, AutoPurchaseResponse.class);
            log.info("▶ [AutoPurchase][{}] Notification envoyée : {}", channel, data);
            strategy.send(data);
        } catch (IllegalArgumentException e) {
            log.warn("⚠ [AutoPurchase] Canal '{}' introuvable : {}", channel, e.getMessage());
        }
    }

    public void autoPurchaseNotifyAllChannels(AutoPurchaseResponse  data) {
        if (!isAutoPurchaseDataValid(data)) {
            log.warn("⚠️ AutoPurchase data invalide ou incomplète, diffusion ignorée : {}", data);
            return;
        }
        log.info("▶ [AutoPurchase] Diffusion sur tous les canaux : {}", data);
        publisher.publishEvent(new NotificationEvent(this, data));
    }

    /* ===================== GENERIC NOTIFICATIONS ===================== */

    public void notifyOne(String channel, NotificationData data) {
        if (!isNotificationDataValid(data)) {
            log.warn("⚠️ NotificationData invalide ou incomplète, notification ignorée : {}", data);
            return;
        }
        try {
            NotificationStrategy<NotificationData> strategy =
                    notificationFactory.getStrategy(channel, NotificationData.class);
            log.info("▶ [Notification][{}] Notification envoyée : {}", channel, data);
            strategy.send(data);
        } catch (IllegalArgumentException e) {
            log.warn("⚠ [Notification] Canal '{}' introuvable : {}", channel, e.getMessage());
        }
    }

    public void notifyAllChannels(NotificationData data) {
        if (!isNotificationDataValid(data)) {
            log.warn("⚠️ NotificationData invalide ou incomplète, diffusion ignorée : {}", data);
            return;
        }
        log.info("▶ [Notification] Diffusion sur tous les canaux : {}", data);
        publisher.publishEvent(new NotificationEvent(this, data));
    }

    /* ===================== VALIDATION ===================== */

    private boolean isNotificationDataValid(NotificationData data) {
        if (data == null) return false;

        // Exemples de critères : au moins un champ utilisateur non vide ET nom produit non vide
        boolean hasUserInfo = (data.getUserName() != null && !data.getUserName().isBlank())
                || (data.getUserEmail() != null && !data.getUserEmail().isBlank())
                || (data.getUserPhoneNumber() != null && !data.getUserPhoneNumber().isBlank());

        boolean hasProductInfo = (data.getProductName() != null && !data.getProductName().isBlank());

        return hasUserInfo && hasProductInfo;
    }

    private boolean isAutoPurchaseDataValid(AutoPurchaseResponse data) {
        if (data == null) return false;
		return true;
        
    }
    
    private boolean isConfirmPendingTransferDataValid(ConfirmPendingTransferData data) {
        if (data == null) return false;
		return true;
        
    }
}


