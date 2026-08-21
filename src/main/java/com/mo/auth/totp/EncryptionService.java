package com.mo.auth.totp;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EncryptionService {

    private final SecretKey key;

    public EncryptionService(@Value("${app.encryption.key}") String keyValue) {
        byte[] keyBytes = null;
        byte[] decoded = null;

        try {
            decoded = Base64.getDecoder().decode(keyValue);
        } catch (IllegalArgumentException ignored) {
            // Ignore invalid Base64 and try raw key bytes below
        }

        if (decoded != null && isValidAesKeyLength(decoded.length)) {
            keyBytes = decoded;
        } else {
            byte[] rawKeyBytes = keyValue.getBytes(StandardCharsets.UTF_8);
            if (isValidAesKeyLength(rawKeyBytes.length)) {
                keyBytes = rawKeyBytes;
            } else {
                throw new IllegalArgumentException("app.encryption.key must be a Base64-encoded AES key or a raw 16/24/32 byte string.");
            }
        }

        this.key = new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
    }

    private boolean isValidAesKeyLength(int length) {
        return length == 16 || length == 24 || length == 32;
    }

    public String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
