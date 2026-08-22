package com.mo.api.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;

import com.mo.notification.strategies.EmailNotificationStrategy;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;


import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/public/test-email")
@RequiredArgsConstructor
public class EmailTestController {

    @Value("${notification.email.from:your-email@example.com}")
    private String fromEmail;

    @Value("${notification.email.sendgrid-api-key:}")
    private String sendgridApiKey;

    @Value("${notification.sms.twilio.account-sid:}")
    private String accountSid;

    @Value("${notification.sms.twilio.auth-token:}")
    private String authToken;

    @Value("${notification.sms.twilio.from:+12314473797}")
    private String fromPhone;

    private static final String PLATFORM_NAME = "Golden Business";


//    @GetMapping
//    public String sendTestEmail() {
//        String subject = "[" + PLATFORM_NAME + "] Confirmation de votre commande";
//
//        // Contenu HTML plus engageant
//        String htmlContent = "<p>Bonjour <strong>Mohamed</strong>,</p>"
//                + "<p>Nous vous remercions pour votre commande sur <strong>" + PLATFORM_NAME + "</strong>.</p>"
//                + "<p>Voici les détails de votre produit :</p>"
//                + "<ul>"
//                + "<li><strong>Nom :</strong> Chemise noire</li>"
//                + "<li><strong>Quantité :</strong> 1</li>"
//                + "<li><strong>Montant :</strong> 10 000 XAF</li>"
//                + "</ul>"
//                + "<p>Nous espérons que vous serez pleinement satisfait.</p>"
//                + "<p>À bientôt sur <strong>" + PLATFORM_NAME + "</strong> !</p>"
//                + "<br><p style='font-size:0.9em;color:#888'>— L'équipe " + PLATFORM_NAME + "</p>";
//
//        try {
//            Email from = new Email(fromEmail, PLATFORM_NAME);
//            Email to = new Email("mosanisangou@gmail.com");
//            Content content = new Content("text/html", htmlContent);
//            Mail mail = new Mail(from, subject, to, content);
//
//            SendGrid sg = new SendGrid(sendgridApiKey);
//            Request req = new Request();
//            req.setMethod(Method.POST);
//            req.setEndpoint("mail/send");
//            req.setBody(mail.build());
//
//            Response resp = sg.api(req);
//            log.info("[EMAIL][SendGrid] Email envoyé : status={}, body={}", resp.getStatusCode(), resp.getBody());
//        } catch (Exception e) {
//            log.error("[EMAIL][SendGrid] Erreur lors de l'envoi de l'email", e);
//        }
//        return "Email envoyé !";
//    }
//    
    
    
    @GetMapping
    @Operation(summary = "Send test SMS", description = "Send a sample SMS message using configured Twilio credentials")
    public String sendTestSms() {
        if (accountSid == null || accountSid.isBlank() || authToken == null || authToken.isBlank() || fromPhone == null || fromPhone.isBlank()) {
            return "Twilio non configuré. Définis TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN et TWILIO_FROM dans Render.";
        }

        String to = "+237657371255"; // remplace par ton vrai numéro de téléphone pour test
        String userName = "Mohamed";
        String productName = "Chemise noire";
        int productQuantity = 1;
        double productAmount = 10000;
        String currency = "XAF";

        String message = "Bonjour " + userName +
                ", votre produit " + productName + " (x" + productQuantity +
                ") est disponible pour " + productAmount + " " + currency +
                " - " + PLATFORM_NAME;

        try {
            Twilio.init(accountSid, authToken);
            Message.creator(new PhoneNumber(to), new PhoneNumber(fromPhone), message).create();
            log.info("[SMS][Twilio] SMS de test envoyé à {}", to);
            return "SMS envoyé avec succès à " + to;
        } catch (Exception e) {
            log.error("[SMS][Twilio] Erreur lors de l'envoi du SMS de test", e);
            return "Échec de l'envoi du SMS : " + e.getMessage();
        }
    }
}
