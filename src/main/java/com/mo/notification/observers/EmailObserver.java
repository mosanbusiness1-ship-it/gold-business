package com.mo.notification.observers;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.mo.core.dtos.NotificationData;
import com.mo.notification.consumers.NotificationConsumers;
import com.mo.notification.events.NotificationEvent;
import com.mo.notification.strategies.EmailNotificationStrategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class EmailObserver implements ApplicationListener<NotificationEvent> {

    private final EmailNotificationStrategy emailStrategy;
    
    @Async("taskExecutor")
    @Override
    public void onApplicationEvent(NotificationEvent event) {
        Object rawData = event.getData();

        try {
            if (rawData instanceof NotificationData data) {
                log.debug("[EMAIL OBSERVER] Début d'envoi d'email pour user={}", data.getUserName());
                emailStrategy.send(data);
                log.debug("[EMAIL OBSERVER] ✅ Email envoyé avec succès");
            } else {
                log.warn("[EMAIL OBSERVER] Type de données non supporté : {}", rawData.getClass().getName());
            }
        } catch (Exception e) {
            log.error("[EMAIL OBSERVER] ❌ Erreur lors de l'envoi d'email", e);
            // Fallback: continuer sans retry (email failure is non-critical)
        }
    }
}

