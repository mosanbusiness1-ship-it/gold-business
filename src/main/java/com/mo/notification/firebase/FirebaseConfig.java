package com.mo.notification.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Bean
    @Lazy
    @ConditionalOnProperty(
        name = "firebase.enabled",
        havingValue = "true",
        matchIfMissing = false
    )
    public FirebaseApp firebaseApp() throws IOException {
        // Charger le fichier JSON depuis le classpath
        InputStream serviceAccount = getClass().getResourceAsStream("/firebase-service-account.json");
        if (serviceAccount == null) {
            log.warn("⚠️ Firebase désactivé: Le fichier firebase-service-account.json est introuvable dans le classpath.");
            return null;
        }

        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                log.info("✅ Firebase initialisé avec succès");
                return FirebaseApp.initializeApp(options);
            } else {
                return FirebaseApp.getInstance();
            }
        } catch (IOException e) {
            log.error("❌ Erreur lors de l'initialisation de Firebase", e);
            throw e;
        }
    }
}

