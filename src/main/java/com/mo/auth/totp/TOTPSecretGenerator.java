package com.mo.auth.totp;

import org.apache.commons.codec.binary.Base32;
import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;

import javax.crypto.SecretKey;


import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import java.security.NoSuchAlgorithmException;

public class TOTPSecretGenerator {

    public static String generateBase32Secret() throws NoSuchAlgorithmException {
        TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator();
        KeyGenerator keyGenerator = KeyGenerator.getInstance(totp.getAlgorithm());
        keyGenerator.init(Mac.getInstance(totp.getAlgorithm()).getMacLength() * 8);
        SecretKey key = keyGenerator.generateKey();

        Base32 base32 = new Base32();
        return base32.encodeToString(key.getEncoded());
    }
}

