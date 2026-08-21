//package com.mo.notification.controllers.listeners;
//
//import com.mo.core.dtos.NotificationData;
//import com.mo.notification.events.NotificationEvent;
//import com.mo.notification.strategies.NotificationStrategy;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.event.EventListener;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class NotificationEventListener {
//
//    private final Map<String, NotificationStrategy<NotificationData>> strategies;
//
//    @EventListener
//    public void onNotificationEvent(NotificationEvent event) {
//        NotificationData data = event.getData();
//
//        for (Map.Entry<String, NotificationStrategy<NotificationData>> entry : strategies.entrySet()) {
//            String channel = entry.getKey();
//            NotificationStrategy<NotificationData> strategy = entry.getValue();
//
//            try {
//                log.info("📢 Envoi notification [{}] via canal: {}", data.getProductName(), channel);
//                strategy.send(data);
//            } catch (Exception e) {
//                log.error("❌ Échec de l'envoi sur le canal {} pour {}", channel, data, e);
//            }
//        }
//    }
//}

