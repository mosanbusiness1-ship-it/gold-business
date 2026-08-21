package com.mo.notification.strategies;


import com.mo.core.dtos.NotificationData;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsNotificationStrategy implements NotificationStrategy<NotificationData> {

    @Value("${notification.sms.twilio.account-sid}")
    private String accountSid;

    @Value("${notification.sms.twilio.auth-token}")
    private String authToken;

    @Value("${notification.sms.twilio.from}")
    private String fromPhone;

    private static final String PLATFORM_NAME = "Golden Business";

    @Override
    public void send(NotificationData payload) {
        String to = payload.getUserPhoneNumber();
        log.warn("[SMS] DEBUT DU TAITEMENT DE L'ENVOIE.");
        if (to == null) {
            log.warn("[SMS] Aucun numéro de téléphone défini.");
            return;
        }

        String message = "Bonjour " + (payload.getUserFullName() != null ? payload.getUserFullName() : "") + ",\n\n" +
                "Une offre est disponible pour votre besoin " + payload.getNeedDescription() +
                "Offre : "+ " (x" + payload.getProductQuantity() + ") " + payload.getProductName() +"prix :" + payload.getProductAmount() + " " + payload.getProductCurrency() + ".\n" +
                "Cliquez ici pour plus de détails : " + payload.getViewDetailsLink() + "\n\n" +
                "Merci pour votre confiance.\nL'équipe " + PLATFORM_NAME;

        try {
            Twilio.init(accountSid, authToken);
            Message.creator(new PhoneNumber(to), new PhoneNumber(fromPhone), message).create();
            log.info("[SMS][Twilio] SMS envoyé à {}", to);
        } catch (Exception e) {
            log.error("[SMS][Twilio] Erreur lors de l'envoi du SMS", e);
        }
    }

    @Override
    public String channel() {
        return "SMS";
    }
    
    @Override
    public Class<NotificationData> getSupportedType() {
        return NotificationData.class;
    }
}

