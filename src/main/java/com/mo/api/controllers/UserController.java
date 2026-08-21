package com.mo.api.controllers;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

import com.mo.auth.User;
import com.mo.core.dtos.UserDTO;
import com.mo.core.dtos.usersDtos.UpdateEmailRequest;
import com.mo.core.dtos.usersDtos.UpdatePhoneRequest;
import com.mo.core.dtos.usersDtos.UpdateExternalWalletIdRequest;
import com.mo.core.services.UserService;
import com.mo.mappers.UserMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/public/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    @Operation(summary = "List users", description = "Return all users as UserDTO objects")
    public List<UserDTO> findAllUsers() {
        List<UserDTO> userDtos = new ArrayList<>();
        Iterable<User> users = userService.findAllUsers();

        users.forEach(user -> userDtos.add(userMapper.toDto(user)));

        return userDtos;
    }

    @PutMapping("/{id}/email")
    @Operation(summary = "Update user email", description = "Update the email address for an existing user")
    public ResponseEntity<?> updateEmail(@PathVariable Long id, @Valid @RequestBody UpdateEmailRequest request) {
        Optional<User> optionalUser = userService.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        User user = optionalUser.get();
        user.setEmail(request.getEmail());
        userService.saveUser(user);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PutMapping("/{id}/phone")
    @Operation(summary = "Update user phone", description = "Update the phone number for an existing user")
    public ResponseEntity<?> updatePhoneNumber(@PathVariable Long id, @Valid @RequestBody UpdatePhoneRequest request) {
        Optional<User> optionalUser = userService.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        User user = optionalUser.get();
        user.setPhoneNumber(request.getPhoneNumber());
        userService.saveUser(user);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PutMapping("/{id}/external-wallet-id")
    @Operation(summary = "Update user external wallet identifier", description = "Update the external wallet identifier for an existing user")
    public ResponseEntity<?> updateExternalWalletId(@PathVariable Long id, @Valid @RequestBody UpdateExternalWalletIdRequest request) {
        Optional<User> optionalUser = userService.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        User user = optionalUser.get();
        user.setExternalWalletId(request.getExternalWalletId());
        userService.saveUser(user);
        return ResponseEntity.ok(userMapper.toDto(user));
    }
}
