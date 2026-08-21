package com.mo.notification.strategies;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mo.core.dtos.NotificationData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushNotificationStrategy implements NotificationStrategy<NotificationData> {

    @Value("${notification.push.fcm.credentials-path}")
    private String firebaseConfigPath;

    private static final String PLATFORM_NAME = "Golden Business";

    @Override
    public void send(NotificationData payload) {
        try {
        	log.warn("[PUSH] DEBUT DU TAITEMENT DE L'ENVOIE.");
            // Exemple simple avec un titre et un corps
            String title = PLATFORM_NAME + " - Nouvelle notification";
            String body = "Produit: " + payload.getProductName() + " (x" + payload.getProductQuantity() + ")";

            // Ici tu dois construire le message via l’API Firebase
            log.info("[PUSH][FCM] Titre: {}, Message: {}", title, body);
            // sendToDevice(token, title, body); // méthode fictive
        } catch (Exception e) {
            log.error("[PUSH][FCM] Erreur lors de l'envoi de la notification push", e);
        }
    }

    @Override
    public String channel() {
        return "PUSH";
    }
    
    @Override
    public Class<NotificationData> getSupportedType() {
        return NotificationData.class;
    }
}

