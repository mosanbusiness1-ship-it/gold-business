package com.mo.configuration;

import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.mo.auth.JwtService;
import com.mo.auth.TokenBlacklistService;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final TokenBlacklistService tokenBlacklistService;
    
    @Autowired
    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver,
            TokenBlacklistService tokenBlacklistService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.tokenBlacklistService = tokenBlacklistService;
    }


    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            
            validateTokenStrictly(jwt, request);
            
            final String username = jwtService.extractUsername(jwt);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            validateUserDetails(userDetails, jwt);
            
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
            );
            
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            
            filterChain.doFilter(request, response);
            
        } catch (Exception exception) {
            logger.error("JWT Validation Error - {} | IP: {} | Path: {}", 
                exception.getMessage(), 
                request.getRemoteAddr(),
                request.getRequestURI());
            
            handlerExceptionResolver.resolveException(request, response, null, 
                new AuthenticationServiceException("Authentication failed", exception));
        }
    }

    private void validateTokenStrictly(String jwt, HttpServletRequest request) {
        if (tokenBlacklistService.isTokenRevoked(jwt)) {
            throw new JwtException("Token revoked");
        }

        if (!jwtService.isTokenValid(jwt)) {
            throw new JwtException("Invalid token signature");
        }

        if (jwtService.isTokenExpired(jwt)) {
            throw new JwtException("Token expired");
        }

        if (!jwtService.extractIssuer(jwt).equals(jwtService.getIssuer())) {
            throw new JwtException("Invalid token issuer");
        }

        String tokenIp = jwtService.extractClaim(jwt, claims -> claims.get("ip", String.class));
        String requestIp = resolveClientIp(request);
        if (tokenIp != null && !"unknown".equals(tokenIp) && !tokenIp.equals(requestIp)) {
            logger.warn("JWT IP mismatch: tokenIp={}, requestIp={}, path={}", tokenIp, requestIp, request.getRequestURI());
        }
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwardedFor)) {
            return forwardedFor.split(",")[0].trim();
        }

        String realIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(realIp)) {
            return realIp;
        }

        return request.getRemoteAddr();
    }

    private void validateUserDetails(UserDetails userDetails, String jwt) {
        if (!userDetails.getUsername().equals(jwtService.extractUsername(jwt))) {
            throw new AuthenticationServiceException("Username mismatch");
        }

        Set<String> tokenRoles = new HashSet<>(jwtService.extractRoles(jwt));
        Set<String> userRoles = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());
            
        if (!userRoles.containsAll(tokenRoles)) {
            throw new AuthenticationServiceException("Role mismatch");
        }

        if (!userDetails.isEnabled()) {
            throw new AuthenticationServiceException("User disabled");
        }
    }
}
