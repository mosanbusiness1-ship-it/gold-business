package com.mo.notification.observers;

import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.mo.notification.events.NotificationEvent;
import com.mo.core.dtos.NotificationData;
import com.mo.notification.strategies.PushNotificationStrategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class PushObserver implements ApplicationListener<NotificationEvent> {

    private final PushNotificationStrategy pushStrategy;

    @Async("taskExecutor")
    @Override
    public void onApplicationEvent(NotificationEvent event) {
        Object rawData = event.getData();

        try {
            if (rawData instanceof NotificationData data) {
                log.debug("[PUSH OBSERVER] Début d'envoi de notification push pour user={}", data.getUserName());
                pushStrategy.send(data);
                log.debug("[PUSH OBSERVER] ✅ Notification push envoyée avec succès");
            } else {
                log.warn("[PUSH OBSERVER] Type de données non supporté : {}", rawData.getClass().getName());
            }
        } catch (Exception e) {
            log.error("[PUSH OBSERVER] ❌ Erreur lors de l'envoi de notification push", e);
            // Fallback: continuer sans retry
        }
    }
}

