package com.mo.notification.observers;

import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.mo.core.dtos.NotificationData;
import com.mo.notification.events.NotificationEvent;

import com.mo.notification.strategies.SmsNotificationStrategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(3)
@RequiredArgsConstructor
public class SmsObserver implements ApplicationListener<NotificationEvent> {

    private final SmsNotificationStrategy smsStrategy;

    @Async("taskExecutor")
    @Override
    public void onApplicationEvent(NotificationEvent event) {
        Object rawData = event.getData();

        try {
            if (rawData instanceof NotificationData data) {
                log.debug("[SMS OBSERVER] Début d'envoi de SMS pour user={}", data.getUserName());
                smsStrategy.send(data);
                log.debug("[SMS OBSERVER] ✅ SMS envoyé avec succès");
            } else {
                log.warn("[SMS OBSERVER] Type de données non supporté : {}", rawData.getClass().getName());
            }
        } catch (Exception e) {
            log.error("[SMS OBSERVER] ❌ Erreur lors de l'envoi de SMS", e);
            // Fallback: continuer sans retry
        }
    }
}






