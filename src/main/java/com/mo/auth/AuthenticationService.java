package com.mo.auth;


import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mo.repositories.UserRepository;

import lombok.Data;

@Service
public class AuthenticationService {
	@Autowired
    private final UserRepository userRepository;
    
    private final RoleRepository roleRepository;
    
    private final PasswordEncoder passwordEncoder;
    
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
    	RoleRepository roleRepository,
        UserRepository userRepository,
        AuthenticationManager authenticationManager,
        PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public User signup(RegisterUserDto input) {
        User user = new User();
        user.setFullName(input.getFullName());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());

        // Assigner les rôles à l'utilisateur en fonction des ID
//        List<Role> roles = roleRepository.findAllById(input.getRoleIds());
//        user.setRoles(roles);

        // Sauvegarder l'utilisateur
        return userRepository.save(user);
    }



    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );
        System.out.println(input.getEmail());
        return userRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }
    
    public User assignRole(Long userId, Role role) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.getRoles().add(role);
        return userRepository.save(user);
    }
    
    public User removeRole(Long userId, Role role) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.getRoles().remove(role);
        return userRepository.save(user);
    }
    
    public User loadUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }
}

