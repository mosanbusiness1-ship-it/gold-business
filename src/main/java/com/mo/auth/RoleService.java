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

        return authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equalsIgnoreCase("ADMIN"));
    }

    // Vérifie si l'utilisateur authentifié a le rôle ROOT
    public boolean isRoot(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return false;

        return authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equalsIgnoreCase("ROOT"));
    }
}

