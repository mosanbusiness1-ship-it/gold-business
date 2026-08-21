package com.mo.notification.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;

import com.mo.core.dtos.autoPurchase.AutoPurchaseNotificationDataDTO;
import com.mo.core.dtos.autoPurchase.AutoPurchaseResponse;
import com.mo.notification.services.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /** Envoi via un canal précis (SMS, EMAIL, PUSH, etc.) */
    @PostMapping("/single")
    @Operation(summary = "Send single notification", description = "Send a notification to a specific channel using the provided payload")
    public void sendSingle(
            @RequestParam String type, 
            @RequestBody AutoPurchaseResponse data) {
        notificationService.autoPurchaseNotifyOne(type, data);
    }

    /** Envoi à tous les canaux disponibles */
    @PostMapping("/multi")
    @Operation(summary = "Send multi-channel notification", description = "Send a notification to all configured channels")
    public void sendMulti(@RequestBody AutoPurchaseResponse data) {
        notificationService.autoPurchaseNotifyAllChannels(data);
    }
}
