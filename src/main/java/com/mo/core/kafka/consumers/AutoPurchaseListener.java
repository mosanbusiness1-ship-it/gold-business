package com.mo.core.kafka.consumers;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mo.core.dtos.autoPurchase.AutoPurchaseResponse;
import com.mo.core.kafka.producers.MessageProducer;

@Component
public class AutoPurchaseListener {

    private final ObjectMapper objectMapper;
    private final MessageProducer messageProducer;

    public AutoPurchaseListener(ObjectMapper objectMapper, MessageProducer messageProducer) {
        this.objectMapper = objectMapper;
        this.messageProducer = messageProducer;
    }

    @KafkaListener(topics = "auto-purchase-completed", groupId = "auto-purchase-group")
    public void handleAutoPurchaseCompleted(String message) {
        try {
            // Désérialisation du message
            AutoPurchaseResponse response = objectMapper.readValue(message, AutoPurchaseResponse.class);

            // Traitement du message
            System.out.println("Transaction confirmée avec succès !");
            System.out.println("Montant : " + response.getAmount());
            System.out.println("Devise : " + response.getCurrency());
            System.out.println("Source : " + response.getSrcChannel());
            System.out.println("Destination : " + response.getDestChannel());
            System.out.println("Raison : " + response.getReason());
            
            messageProducer.send("notify-auto-purchase-completed", message);
        } catch (JsonProcessingException e) {
            System.err.println("Erreur lors de la désérialisation du message : " + e.getMessage());
        }
    }
}



//
//import java.util.Optional;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.annotation.KafkaListener;
//
//import org.springframework.stereotype.Component;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.mo.core.dtos.autoPurchase.AutoPurchaseResponseDTO;
//import com.mo.core.kafka.producers.MessageProducer;
//import com.mo.core.services.UserService;
//import com.mo.auth.User;
//import com.mo.core.dtos.autoPurchase.AutoPurchaseNotificationDataDTO; 
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Component
//public class AutoPurchaseResultConsumer {
//
//    private final MessageProducer messageProducer;
//    private final UserService userService;
//    
//    private final ObjectMapper objectMapper;
//
//    public AutoPurchaseResultConsumer(MessageProducer messageProducer, UserService userService) {
//        this.messageProducer = messageProducer;
//        this.userService = userService;
//		this.objectMapper = new ObjectMapper();
//    }
//
//    @KafkaListener(topics = "auto-purchase-completed", groupId = "gb")
//    public void onCompleted(String jsonMessage) {
//        try {
//            AutoPurchaseResponseDTO response = objectMapper.readValue(jsonMessage, AutoPurchaseResponseDTO.class);
//            log.info("✅ Paiement complété : {}", response);
//
//            AutoPurchaseNotificationDataDTO notificationData = buildNotificationData(response);
//            if (notificationData != null) {
//                String json = objectMapper.writeValueAsString(notificationData);
//                messageProducer.send("notify-auto-purchase-completed", json);
//                log.info("📢 Notification envoyée pour paiement complété.");
//            } else {
//                log.warn("⚠ Impossible de construire la notification pour {}", response);
//            }
//
//        } catch (Exception e) {
//            log.error("Erreur lors du traitement du message Kafka 'auto-purchase-completed'", e);
//        }
//    }
//
//    @KafkaListener(topics = "auto-purchase-failed", groupId = "gb")
//    public void onFailed(String jsonMessage) {
//        try {
//            AutoPurchaseResponseDTO response = objectMapper.readValue(jsonMessage, AutoPurchaseResponseDTO.class);
//            log.warn("❌ Paiement échoué : {}", response);
//
//            AutoPurchaseNotificationDataDTO notificationData = buildNotificationData(response);
//            if (notificationData != null) {
//                String json = objectMapper.writeValueAsString(notificationData);
//                messageProducer.send("notify-auto-purchase-failed", json);
//                log.info("📢 Notification envoyée pour paiement échoué.");
//            } else {
//                log.warn("⚠ Impossible de construire la notification pour {}", response);
//            }
//
//        } catch (Exception e) {
//            log.error("Erreur lors du traitement du message Kafka 'auto-purchase-failed'", e);
//        }
//    }
//
//    private AutoPurchaseNotificationDataDTO buildNotificationData(AutoPurchaseResponseDTO response) {
//        if (response == null || response.getRequest() == null) {
//            return null;
//        }
//
//        String externalWalletIdSrc = response.getRequest().getExternalWalletIdSrc();
//        String externalWalletIdDest = response.getRequest().getExternalWalletIdDest();
//
//        Optional<User> userOpt = userService.findByExternalWalletId(externalWalletIdSrc);
//        Optional<User> ownerOpt = userService.findByExternalWalletId(externalWalletIdDest);
//
//        AutoPurchaseNotificationDataDTO notificationData = new AutoPurchaseNotificationDataDTO();
//
//        userOpt.ifPresent(user -> {
//            notificationData.setUserEmail(user.getEmail());
//            notificationData.setUserphoneNumber(user.getPhoneNumber());
//        });
//
//        ownerOpt.ifPresent(owner -> {
//            notificationData.setOwnerEmail(owner.getEmail());
//            notificationData.setOwnerPhoneNumber(owner.getPhoneNumber());
//        });
//
//        return notificationData;
//    }
//}
