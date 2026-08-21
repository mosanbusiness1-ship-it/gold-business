package com.mo.api.controllers;

import com.mo.auth.Role;
import com.mo.auth.RoleService;
import com.mo.core.dtos.CreateRoleRequest;
import com.mo.repositories.UserRoleRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;

@CrossOrigin
@RequestMapping("/public/roles")
@RestController
public class RoleController {

    private final RoleService roleService;
    

    public RoleController(RoleService roleService, UserRoleRepository serRoleRepository) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @Operation(summary = "Create role", description = "Create a new role in the system")
    public ResponseEntity<Role> createRole(@RequestBody CreateRoleRequest request) {
        Role createdRole = roleService.createRole(request.getName());
        return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
    }
    

    // Vérifie si l'utilisateur est un administrateur
    @GetMapping("/check-admin")
    @Operation(summary = "Check admin", description = "Return true when the authenticated user has admin privileges")
    public boolean isAdmin(Authentication authentication) {
        return roleService.isAdmin(authentication);
    }

    // Vérifie si l'utilisateur est un root
    @GetMapping("/check-root")
    @Operation(summary = "Check root", description = "Return true when the authenticated user has root privileges")
    public boolean isRoot(Authentication authentication) {
        return roleService.isRoot(authentication);
    }
    
    // Récupérer les rôles de l'utilisateur actuellement authentifié
    @GetMapping("/roles")
    @Operation(summary = "Get roles", description = "Return the list of granted authorities for the authenticated user")
    public List<String> getRoles(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)  // Récupérer le nom du rôle
                .collect(Collectors.toList());
    }
    

   
}

