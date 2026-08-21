package com.mo.core.services;

import com.mo.core.dtos.UserRoleDTO;
import com.mo.repositories.UserRoleRepository;
import org.springframework.stereotype.Service;

@Service
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    public UserRoleService(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    // Méthode pour ajouter un rôle à un utilisateur
    public void addUserRole(UserRoleDTO userRoleDTO) {
        userRoleRepository.addUserRole(userRoleDTO.getUserId(), userRoleDTO.getRoleId());
    }
}

