package com.mo.auth;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final JwtService jwtService;
    private final ConcurrentMap<String, Long> revokedTokens = new ConcurrentHashMap<>();

    public void revokeToken(String token) {
        try {
            Date expirationDate = jwtService.extractExpiration(token);
            long ttl = calculateTTL(expirationDate);
            if (ttl > 0) {
                revokedTokens.put(token, System.currentTimeMillis() + ttl);
            }
        } catch (JwtException e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    public boolean isTokenRevoked(String token) {
        cleanupExpiredTokens();
        return revokedTokens.containsKey(token);
    }

    public long getRevokedTokensCount() {
        cleanupExpiredTokens();
        return revokedTokens.size();
    }

    private void cleanupExpiredTokens() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, Long>> iterator = revokedTokens.entrySet().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getValue() <= now) {
                iterator.remove();
            }
        }
    }

    private long calculateTTL(Date expirationDate) {
        return expirationDate.getTime() - System.currentTimeMillis();
    }
}
