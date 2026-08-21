package com.mo.auth.totp;

import org.apache.commons.codec.binary.Base32;

import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;

public class TOTPValidator {

    private final TimeBasedOneTimePasswordGenerator totp;

    public TOTPValidator() throws NoSuchAlgorithmException {
        this.totp = new TimeBasedOneTimePasswordGenerator();
    }

    public boolean validateCode(String base32Secret, String code) throws Exception {
        Base32 base32 = new Base32();
        byte[] secretBytes = base32.decode(base32Secret);
        SecretKeySpec keySpec = new SecretKeySpec(secretBytes, totp.getAlgorithm());

        String expectedCode = totp.generateOneTimePasswordString(keySpec, Instant.now());
        return expectedCode.equals(code);
    }
}


