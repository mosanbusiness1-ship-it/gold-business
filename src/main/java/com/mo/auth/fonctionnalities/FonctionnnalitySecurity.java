package com.mo.auth.fonctionnalities;

import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.mo.auth.FonctionnalityService;
import com.mo.auth.Role;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class FonctionnnalitySecurity {

    @Autowired
    private FonctionnalityService fonctionnalityService;

    public boolean hasAccess(Authentication authentication, HttpServletRequest request) {
        String fonctionnaliteName = extractFonctionnaliteFromRequest(request);

        Set<Role> allowedRoles = fonctionnalityService.getRolesForFonctionnalite(fonctionnaliteName);

        // Récupérer les rôles de l'utilisateur connecté
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            if (allowedRoles.stream().anyMatch(role -> role.getName().equals(authority.getAuthority()))) {
                return true;
            }
        }

        return false;
    }

    private String extractFonctionnaliteFromRequest(HttpServletRequest request) {
        // Ici, vous pouvez extraire le nom de la fonctionnalité à partir de l'URL ou d'une autre source.
        // Exemple : "/api/fonctionnalite1" => "fonctionnalite1"
        String path = request.getRequestURI();
        return path.split("/")[2]; // Supposons que la fonctionnalité est dans la deuxième partie de l'URL
    }
}
