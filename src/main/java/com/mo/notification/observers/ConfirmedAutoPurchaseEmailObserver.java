package com.mo.notification.observers;

import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.mo.core.dtos.autoPurchase.AutoPurchaseResponse;
import com.mo.notification.events.NotificationEvent;
import com.mo.notification.strategies.EmailConfirmedTransactionNotificationStrategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(4)
@RequiredArgsConstructor
public class ConfirmedAutoPurchaseEmailObserver implements ApplicationListener<NotificationEvent> {

    private final EmailConfirmedTransactionNotificationStrategy emailStrategy;

    @Async("taskExecutor")
	@Override
	public void onApplicationEvent(NotificationEvent event) {
	    Object rawData = event.getData();
	
	    try {
	        if (rawData instanceof AutoPurchaseResponse data) {
	            log.debug("[CONFIRMED_AUTOPURCHASE_EMAIL] Envoi email pour auto-achat confirmé amount={} {}", data.getAmount(), data.getCurrency());
	            emailStrategy.send(data);
	            log.debug("[CONFIRMED_AUTOPURCHASE_EMAIL] ✅ Email envoyé avec succès");
	        } else {
	            log.warn("[CONFIRMED_AUTOPURCHASE_EMAIL] Type de données non supporté : {}", rawData.getClass().getName());
	        }
	    } catch (Exception e) {
	        log.error("[CONFIRMED_AUTOPURCHASE_EMAIL] ❌ Erreur lors de l'envoi d'email", e);
	    }
	}
}
