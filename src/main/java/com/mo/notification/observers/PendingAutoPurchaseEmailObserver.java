package com.mo.notification.observers;

import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.mo.core.dtos.autoPurchase.ConfirmPendingTransferData;
import com.mo.notification.events.NotificationEvent;
import com.mo.notification.strategies.EmailPendingAutoPurchaseNotificationStrategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(5)
@RequiredArgsConstructor
public class PendingAutoPurchaseEmailObserver implements ApplicationListener<NotificationEvent> {

    private final EmailPendingAutoPurchaseNotificationStrategy emailStrategy;

    @Async("taskExecutor")
	@Override
	public void onApplicationEvent(NotificationEvent event) {
	    Object rawData = event.getData();
	
	    try {
	        if (rawData instanceof ConfirmPendingTransferData data) {
	            log.debug("[PENDING_AUTOPURCHASE_EMAIL] Envoi email pour auto-achat en attente reason={}", data.getTransactionReason());
	            emailStrategy.send(data);
	            log.debug("[PENDING_AUTOPURCHASE_EMAIL] ✅ Email envoyé avec succès");
	        } else {
	            log.warn("[PENDING_AUTOPURCHASE_EMAIL] Type de données non supporté : {}", rawData.getClass().getName());
	        }
	    } catch (Exception e) {
	        log.error("[PENDING_AUTOPURCHASE_EMAIL] ❌ Erreur lors de l'envoi d'email", e);
	    }
	}
}