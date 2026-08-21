package com.mo.notification.strategies;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.SendGrid;

import com.mo.core.dtos.autoPurchase.AutoPurchaseResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailConfirmedTransactionNotificationStrategy implements NotificationStrategy<AutoPurchaseResponse> {

    @Value("${notification.email.from}")
    private String fromEmail;

    @Value("${notification.email.sendgrid-api-key}")
    private String sendgridApiKey;

    private static final String PLATFORM_NAME = "Golden Business";

    @Override
    public void send(AutoPurchaseResponse response) {
        sendEmail(
                response.getSrcChannel(),
                PLATFORM_NAME + " - Confirmation de transaction",
                buildBuyerMessage(response)
        );

        sendEmail(
                response.getDestChannel(),
                PLATFORM_NAME + " - Vente",
                buildSellerMessage(response)
        );
    }

    private String buildBuyerMessage(AutoPurchaseResponse response) {
        return new StringBuilder()
                .append("Bonjour,\n\n")
                .append("Votre transaction a été confirmée avec succès sur ").append(PLATFORM_NAME).append(" !\n\n")
                .append("Détails de la transaction :\n")
                .append(" - Montant : ").append(response.getAmount()).append(" ").append(response.getCurrency()).append("\n")
                .append(" - Source : ").append(response.getSrcChannel()).append("\n")
                .append(" - Destination : ").append(response.getDestChannel()).append("\n")
                .append(" - Raison : ").append(response.getReason()).append("\n\n")
                .append("Merci d'utiliser ").append(PLATFORM_NAME).append(".\n\n")
                .append("Cordialement,\n")
                .append("L'équipe ").append(PLATFORM_NAME)
                .toString();
    }

    private String buildSellerMessage(AutoPurchaseResponse response) {
        return new StringBuilder()
                .append("Bonjour,\n\n")
                .append("Votre produit a été payé sur ").append(PLATFORM_NAME).append(" !\n\n")
                .append("Détails de la transaction :\n")
                .append(" - Montant : ").append(response.getAmount()).append(" ").append(response.getCurrency()).append("\n")
                .append(" - Source : ").append(response.getSrcChannel()).append("\n")
                .append(" - Raison : ").append(response.getReason()).append("\n\n")
                .append("Merci d'utiliser ").append(PLATFORM_NAME).append(".\n\n")
                .append("Cordialement,\n")
                .append("L'équipe ").append(PLATFORM_NAME)
                .toString();
    }

    private void sendEmail(String to, String subject, String body) {
        if (to == null || to.isEmpty()) {
            log.warn("[EMAIL] Aucun email de destinataire trouvé.");
            return;
        }

        try {
            Email from = new Email(fromEmail, PLATFORM_NAME);
            Email toEmail = new Email(to);
            Content content = new Content("text/plain", body);

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
    public Class<AutoPurchaseResponse> getSupportedType() {
        return AutoPurchaseResponse.class;
    }
}
