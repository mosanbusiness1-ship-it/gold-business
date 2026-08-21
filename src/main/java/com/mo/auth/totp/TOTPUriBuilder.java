package com.mo.auth.totp;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class TOTPUriBuilder {

    public static String buildUri(String issuer, String accountName, String base32Secret) {
        try {
            String encodedIssuer = URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
            String encodedAccountName = URLEncoder.encode(accountName, "UTF-8").replace("+", "%20");
            return String.format(
                    "otpauth://totp/%s:%s?secret=%s&issuer=%s",
                    encodedIssuer, encodedAccountName, base32Secret, encodedIssuer);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 encoding not supported", e);
        }
    }
}


