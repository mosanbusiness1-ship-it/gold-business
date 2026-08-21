package com.mo.auth;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mo.repositories.FonctionnalityRepository;

@Service
public class FonctionnalityService {

    @Autowired
    private FonctionnalityRepository fonctionnalityRepository;

    public Set<Role> getRolesForFonctionnalite(String fonctionnaliteName) {
        return fonctionnalityRepository.findByName(fonctionnaliteName)
            .orElseThrow(() -> new RuntimeException("Fonctionnalite not found"))
            .getRoles();
    }
}
