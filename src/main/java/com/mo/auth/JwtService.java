package com.mo.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.mo.core.enums.MemberType;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${security.jwt.secret-key:change-me}")
    private String secretKey;

    @Value("${security.jwt.expiration-time:36000000}")
    private long jwtExpiration;

    @Value("${security.jwt.issuer:mon-serveur-auth}")
    private String issuer;

    @Value("${security.jwt.invitation-expiration-ms:604800000}")
    private long invitationExpirationMs;

    public String extractUsername(String token) throws JwtException {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractIssuer(String token) {
        return extractClaim(token, Claims::getIssuer);
    }

    public String getIssuer() {
        return issuer;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws JwtException {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        claims.put("ip", getCurrentRequestIp());
        return buildToken(claims, userDetails.getUsername());
    }

    private String buildToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(issuer)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateInvitationToken(Long organisationId, Long inviterUserId, String invitedEmail, MemberType role) {
        return generateInvitationToken(organisationId, inviterUserId, invitedEmail, role, invitationExpirationMs);
    }

    public String generateInvitationToken(Long organisationId, Long inviterUserId, String invitedEmail, MemberType role, long validityInMillis) {
        if (invitedEmail == null || invitedEmail.isBlank()) {
            throw new IllegalArgumentException("Invited email is required");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("organisationId", organisationId);
        claims.put("inviterId", inviterUserId);
        claims.put("email", invitedEmail);
        claims.put("role", (role != null ? role.name() : MemberType.FULL_MEMBER.name()));
        claims.put("type", "INVITATION");
        claims.put("validityMillis", validityInMillis);

        Date now = new Date();
        Date expiration = new Date(now.getTime() + Math.max(validityInMillis, 1L));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(invitedEmail)
                .setIssuer(issuer)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token) &&
                   verifyTokenIssuer(token) &&
                   verifyTokenSignature(token);
        } catch (JwtException ex) {
            logger.warn("Invalid JWT token: {}", ex.getMessage());
            return false;
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return isTokenValid(token) &&
               extractUsername(token).equals(userDetails.getUsername());
    }

    private boolean verifyTokenIssuer(String token) {
        return issuer.equals(extractIssuer(token));
    }

    private boolean verifyTokenSignature(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException ex) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> {
            List<?> rawRoles = claims.get("roles", List.class);
            if (rawRoles == null) {
                return Collections.emptyList();
            }
            return rawRoles.stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList());
        });
    }

    public boolean hasRole(String token, String role) {
        List<String> roles = extractRoles(token);
        return roles != null && roles.contains(role);
    }

    private Claims extractAllClaims(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Claims extractInvitationClaims(String token) {
        Claims claims = extractAllClaims(token);
        if (!"INVITATION".equals(claims.get("type"))) {
            throw new JwtException("Invalid token type");
        }
        return claims;
    }

    private Key getSignInKey() {
        String value = secretKey == null ? "" : secretKey.trim();
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(value);
        } catch (IllegalArgumentException ex) {
            keyBytes = value.getBytes(StandardCharsets.UTF_8);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String getCurrentRequestIp() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attrs.getRequest();

            String forwardedFor = request.getHeader("X-Forwarded-For");
            if (StringUtils.hasText(forwardedFor)) {
                return forwardedFor.split(",")[0].trim();
            }

            String realIp = request.getHeader("X-Real-IP");
            if (StringUtils.hasText(realIp)) {
                return realIp;
            }

            return request.getRemoteAddr();
        } catch (Exception e) {
            return "unknown";
        }
    }
    
}
