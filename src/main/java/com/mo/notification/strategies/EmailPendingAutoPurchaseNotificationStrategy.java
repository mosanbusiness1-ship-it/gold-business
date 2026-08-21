package com.mo.notification.strategies;

import com.mo.core.dtos.autoPurchase.ConfirmPendingTransferData;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailPendingAutoPurchaseNotificationStrategy implements NotificationStrategy<ConfirmPendingTransferData> {

    @Value("${notification.email.from}")
    private String fromEmail;

    @Value("${notification.email.sendgrid-api-key}")
    private String sendgridApiKey;

    private static final String PLATFORM_NAME = "Golden Business";

    @Override
    public void send(ConfirmPendingTransferData payload) {
        // Détermination de l'email du destinataire
        String to = payload.getNotificationChannel() != null ? payload.getNotificationChannel() : payload.getNotificationChannel();
        if (to == null) {
            log.warn("[EMAIL] Aucun destinataire.");
            return;
        }

        // Objet du mail
        String subject = PLATFORM_NAME +  "Paiement automatique";
        // Construction du corps du message
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("Bonjour,\n\n")
                .append("Veillez acceder au lin ci-dessous pour confirmer un paiement automatique sur ").append(PLATFORM_NAME).append("\n")
                .append(" - Details : ").append(payload.getTransactionReason()).append("\n")
                .append(payload.getPayLink());

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
	public Class<ConfirmPendingTransferData> getSupportedType() {
		
		return ConfirmPendingTransferData.class;
	}

}


