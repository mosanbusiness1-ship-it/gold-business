package com.mo.api.controllers;

import com.mo.core.dtos.UserRoleDTO;
import com.mo.core.services.UserRoleService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;


@RestController
@CrossOrigin("*")
@RequestMapping("/api/user_roles")
public class UserRoleController {

    private final UserRoleService userRoleService;

    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    // Ajouter un rôle pour un utilisateur
    @PostMapping
    @Operation(summary = "Add user role", description = "Assign a role to a user using the provided payload")
    public void addUserRole(@RequestBody UserRoleDTO userRoleDTO) {
        userRoleService.addUserRole(userRoleDTO);
    }
}
