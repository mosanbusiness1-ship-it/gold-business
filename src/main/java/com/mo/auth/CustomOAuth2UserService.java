package com.mo.auth;

import java.util.Date;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.mo.repositories.UserRepository;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        // Extraire infos utilisateur du fournisseur (exemple : email)
        String email = oAuth2User.getAttribute("email");

        // Chercher ou créer utilisateur local
        User user = userRepository.findByEmail(email)
                        .orElseGet(() -> createUser(oAuth2User));

        // Mettre à jour les attributs OAuth (pour l’interface OAuth2User)
        user.setAttributes(oAuth2User.getAttributes());

        return user;
    }

    private User createUser(OAuth2User oAuth2User) {
        User user = new User();
        user.setEmail(oAuth2User.getAttribute("email"));
        user.setFullName(oAuth2User.getAttribute("name"));
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        // tu peux définir un mot de passe vide ou généré si besoin (OAuth n'en nécessite pas)
        user.setPassword("");
        user.setTotpEnabled(false);
        user.setAttributes(oAuth2User.getAttributes());
        // ... assigne les rôles par défaut par exemple
        return userRepository.save(user);
    }

}
