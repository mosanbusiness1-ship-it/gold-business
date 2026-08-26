package com.mo.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // Créer un nouveau rôle
    public Role createRole(String roleName) {
        if (roleRepository.existsByName(roleName)) {
            throw new IllegalArgumentException("Le rôle existe déjà : " + roleName);
        }
        Role role = new Role();
        role.setName(roleName.toUpperCase()); // Par convention
        return roleRepository.save(role);
    }

    // Vérifie si l'utilisateur authentifié a le rôle ADMIN
    public boolean isAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return false;

        return hasRole(authentication, "ADMIN");
    }

    // Vérifie si l'utilisateur authentifié a le rôle ROOT
    public boolean isRoot(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return false;
        return hasRole(authentication, "ROOT");
    }

    private boolean hasRole(Authentication authentication, String roleName) {
        if (authentication == null) return false;
        return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
            .anyMatch(a -> {
                if (a == null) return false;
                String normalized = a;
                if (normalized.startsWith("ROLE_")) {
                    normalized = normalized.substring(5);
                }
                return normalized.equalsIgnoreCase(roleName) || a.equalsIgnoreCase(roleName) || a.equalsIgnoreCase("ROLE_" + roleName);
            });
    }
}

