package com.mo.auth.totp;

import org.springframework.stereotype.Service;

import com.mo.auth.User;
import com.mo.repositories.UserRepository;

import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base32;


@Service
public class TotpService {

    private final UserTotpSecretRepository repository;
    private final EncryptionService encryptionService;
    private final TOTPValidator validator;
    private final UserRepository userRepository;

    public TotpService(UserTotpSecretRepository repository, EncryptionService encryptionService, UserRepository userRepository) throws NoSuchAlgorithmException {
        this.repository = repository;
        this.encryptionService = encryptionService;
        this.validator = new TOTPValidator();
        this.userRepository = userRepository;
    }

    /**
     * Génère un nouveau secret TOTP, le chiffre et le stocke pour l'utilisateur.
     */
    public String registerTotpForUser(Long userId) throws Exception {
        System.out.println("[registerTotpForUser] Recherche de l'utilisateur avec ID = " + userId);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new Exception("Utilisateur introuvable"));

        System.out.println("[registerTotpForUser] Génération d'un secret TOTP Base32...");
        String base32Secret = TOTPSecretGenerator.generateBase32Secret();
        System.out.println("[registerTotpForUser] Secret généré : " + base32Secret);

        System.out.println("[registerTotpForUser] Chiffrement du secret...");
        String encrypted = encryptionService.encrypt(base32Secret);
        System.out.println("[registerTotpForUser] Secret chiffré : " + encrypted);

        UserTotpSecret userTotpSecret = new UserTotpSecret();
        userTotpSecret.setUser(user);
        userTotpSecret.setEncryptedBase32Secret(encrypted);
        userTotpSecret.setEnabled(true);

        UserTotpSecret u = repository.save(userTotpSecret);
        System.out.println(u);
        System.out.println("[registerTotpForUser] Secret enregistré en base pour l'utilisateur ID = " + userId);

        return base32Secret;
    }

    /**
     * Construit l'URI OTPAUTH utilisée pour générer un QR code dans une application TOTP.
     */
    public String getTotpUri(Long userId, String issuer, String accountName) throws Exception {
        System.out.println("[getTotpUri] Construction de l'URI TOTP pour l'utilisateur ID = " + userId);
        UserTotpSecret userTotpSecret = repository.findByUserId(userId)
        	    .orElseThrow(() -> new Exception("User TOTP secret not found"));


        System.out.println("[getTotpUri] Secret chiffré trouvé : " + userTotpSecret.getEncryptedBase32Secret());

        try {
            System.out.println("[getTotpUri] Déchiffrement du secret...");
            String decryptedSecret = encryptionService.decrypt(userTotpSecret.getEncryptedBase32Secret());
            System.out.println("[getTotpUri] Secret déchiffré : " + decryptedSecret);

            String uri = TOTPUriBuilder.buildUri(issuer, accountName, decryptedSecret);
            System.out.println("[getTotpUri] URI générée : " + uri);
            return uri;
        } catch (Exception e) {
            System.err.println("[getTotpUri][ERREUR] Erreur lors du déchiffrement du secret : " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Valide le code TOTP fourni par l'utilisateur.
     */
    public boolean verifyCode(Long userId, String code) throws Exception {
        System.out.println("[verifyCode] Vérification du code TOTP pour l'utilisateur ID = " + userId + " avec code = " + code);

        UserTotpSecret userTotpSecret = repository.findByUserId(userId)
            .orElseThrow(() -> new Exception("User TOTP secret not found"));

        System.out.println("[verifyCode] Secret chiffré récupéré : " + userTotpSecret.getEncryptedBase32Secret());

        try {
            System.out.println("[verifyCode] Tentative de déchiffrement du secret...");
            String decryptedSecret = encryptionService.decrypt(userTotpSecret.getEncryptedBase32Secret());
            System.out.println("[verifyCode] Secret déchiffré : " + decryptedSecret);

            TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator(); // par défaut 30s
            Base32 base32 = new Base32();
            SecretKey key = new SecretKeySpec(base32.decode(decryptedSecret), "RAW");

            Instant now = Instant.now();
            Duration step = totp.getTimeStep();

            // Vérifie pour les fenêtres -1, 0 et +1
            for (int i = -1; i <= 1; i++) {
                Instant instantToCheck = now.plus(step.multipliedBy(i));
                String expectedCode = String.format("%06d", totp.generateOneTimePassword(key, instantToCheck));

                System.out.printf("🕒 Fenêtre %d — Code attendu : %s%n", i, expectedCode);
                if (expectedCode.equals(code)) {
                    System.out.println("[verifyCode] ✅ Code TOTP VALIDE dans la fenêtre " + i);
                    return true;
                }
            }

            System.out.println("[verifyCode] ❌ Code TOTP invalide dans toutes les fenêtres");
            return false;

        } catch (Exception e) {
            System.err.println("[verifyCode][ERREUR] Échec du déchiffrement ou de la validation du code : " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


}




