package com.mo.notification.factory;

import com.mo.notification.strategies.NotificationStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Une factory générique capable de retourner la stratégie adaptée
 * pour n'importe quel type de notification (NotificationData, AutoPurchaseNotificationDataDTO, etc.).
 */
@Component
public class NotificationFactory {

    /**
     * Map des stratégies par canal et par type.
     * La clé sera de la forme : "EMAIL-NotificationData", "SMS-AutoPurchaseNotificationDataDTO".
     */
    private final Map<String, NotificationStrategy<?>> strategies;

    public NotificationFactory(List<NotificationStrategy<?>> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        s -> buildKey(s.channel(), s),
                        s -> s
                ));
    }

    /**
     * Récupère une stratégie pour un type et un canal donné.
     *
     * @param channel canal de notification (EMAIL, SMS, PUSH...)
     * @param clazz   classe du type de notification
     * @param <T>     type du payload
     * @return une implémentation de NotificationStrategy<T>
     */
    @SuppressWarnings("unchecked")
    public <T> NotificationStrategy<T> getStrategy(String channel, Class<T> clazz) {
        if (channel == null || channel.trim().isEmpty()) {
            throw new IllegalArgumentException("Canal de notification invalide");
        }

        String key = channel.toUpperCase() + "-" + clazz.getSimpleName();
        NotificationStrategy<?> strategy = strategies.get(key);

        if (strategy == null) {
            throw new IllegalArgumentException(
                    "Aucune stratégie de notification trouvée pour le canal: " + channel +
                    " et le type: " + clazz.getSimpleName()
            );
        }

        return (NotificationStrategy<T>) strategy;
    }

    /**
     * Construit une clé unique pour identifier chaque stratégie.
     */
    private static String buildKey(String channel, NotificationStrategy<?> strategy) {
        // Exemple : "EMAIL-NotificationData" ou "SMS-AutoPurchaseNotificationDataDTO"
        return channel.toUpperCase() + "-" + strategy.getSupportedType().getSimpleName();
    }
}
