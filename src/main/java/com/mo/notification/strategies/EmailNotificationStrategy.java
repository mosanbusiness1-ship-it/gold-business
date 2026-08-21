package com.mo.notification.strategies;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mo.core.dtos.NotificationData;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationStrategy implements NotificationStrategy<NotificationData> {

    @Value("${notification.email.from}")
    private String fromEmail;

    @Value("${notification.email.sendgrid-api-key}")
    private String sendgridApiKey;

    private static final String PLATFORM_NAME = "Golden Business";

    @Override
    public void send(NotificationData payload) {
        // Déterminer l'email du destinataire
    	log.warn("[EMAIL] DEBUT DU TAITEMENT DE L'ENVOIE.");
        String to = payload.getUserEmail();
        if (to == null) {
            log.warn("[EMAIL] Aucun destinataire défini.");
            return;
        }

        // Sujet du mail
        String subject = "[" + PLATFORM_NAME + "] Notification produit";

        // Contenu du mail
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("Bonjour ").append(payload.getUserName() != null ? payload.getUserName() : "").append(",\n\n")
                .append("Offre disponible pour votre besoin"+ payload.getNeedDescription() + ":\n")
                .append(" - Nom : ").append(payload.getProductName()).append("\n")
                .append(" - Quantité : ").append(payload.getProductQuantity()).append("\n")
                .append(" - Montant : ").append(payload.getProductAmount()).append(" ").append(payload.getProductCurrency()).append("\n")
                .append(" - Cliquez sur ce lien pour voir les détails : ").append(payload.getViewDetailsLink()).append("\n\n")
                .append("Merci pour votre confiance.\nL'équipe ").append(PLATFORM_NAME);

        try {
            Email from = new Email(fromEmail, PLATFORM_NAME);
            Email toEmail = new Email(to);
            Content content = new Content("text/plain", contentBuilder.toString());
            Mail mail = new Mail(from, subject, toEmail, content);

            SendGrid sg = new SendGrid(sendgridApiKey);
            Request req = new Request();
            req.setMethod(Method.POST);
            req.setEndpoint("mail/send");
            req.setBody(mail.build());

            Response resp = sg.api(req);
            log.info("[EMAIL][SendGrid] Email envoyé : status={}, body={}", resp.getStatusCode(), resp.getBody());
        } catch (Exception e) {
            log.error("[EMAIL][SendGrid] Erreur lors de l'envoi de l'email", e);
        }
    }

    @Override
    public String channel() {
        return "EMAIL";
    }
    
    @Override
    public Class<NotificationData> getSupportedType() {
        return NotificationData.class;
    }
}

