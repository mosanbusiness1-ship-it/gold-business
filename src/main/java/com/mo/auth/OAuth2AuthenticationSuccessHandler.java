package com.mo.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.mo.repositories.UserRepository;
import com.mo.auth.RoleRepository;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoleRepository  roleRepository;

    public OAuth2AuthenticationSuccessHandler(JwtService jwtService, UserRepository userRepository, RoleRepository roleRepository) {
        this.jwtService = jwtService;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository; 
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName(); // ou: oidcUser.getAttribute("name")

        // Recherche de l'utilisateur
        Optional<User> existingUser = userRepository.findByEmail(email);

        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            // Création automatique du user
        	Role userRole = roleRepository.findByName("ROLE_USER")  // ou crée-le manuellement si nécessaire
        	        .orElseThrow(() -> new RuntimeException("Rôle ROLE_USER non trouvé"));

        	user = new User();
        	user.setEmail(email);
        	user.setFullName(name);
        	user.setPassword(""); // ou "oauth2"
        	user.setTotpEnabled(false);
        	user.setRoles(Collections.singletonList(userRole)); // ✅ ici on met une liste
        	user = userRepository.save(user);
        }

        String token = jwtService.generateToken(user);

        String redirectUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/login")
                .queryParam("token", token)
                .build().toUriString();

        response.sendRedirect(redirectUrl);
    }


}



