package com.mo.core.services;


import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.mo.auth.User;

import com.mo.repositories.UserRepository;

@Service
public class UserService {

    private final UserRepository myUserRepository;

    @Autowired
    public UserService(UserRepository myUserRepository) {
        this.myUserRepository = myUserRepository;
    }

    public User saveUser(User user) {
        return myUserRepository.save(user);
    }

    public Iterable<User> findAllUsers() {
        return myUserRepository.findAll();
    }

    public User findByEmail(String email) {
        return myUserRepository.findByEmail(email).orElse(null);
    }

    public Optional<User> findById(Long id) {
        return myUserRepository.findById(id);
    }

    public void deleteById(Long id) {
        myUserRepository.deleteById(id);
    }

    public Optional<User> getUserById(Long userId) {
        return myUserRepository.findById(userId);
    }

    public Optional<User> findByExternalWalletId(String externalWalletId) {
        return myUserRepository.findByExternalWalletId(externalWalletId);
    }

    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return myUserRepository.findByPhoneNumber(phoneNumber);
    }

    // 🔁 MISE À JOUR : externalWalletId
    public Optional<User> updateExternalWalletId(Long userId, String newExternalWalletId) {
        return myUserRepository.findById(userId).map(user -> {
            user.setExternalWalletId(newExternalWalletId);
            return myUserRepository.save(user);
        });
    }

    // 🔁 MISE À JOUR : phoneNumber
    public Optional<User> updatePhoneNumber(Long userId, String newPhoneNumber) {
        return myUserRepository.findById(userId).map(user -> {
            user.setPhoneNumber(newPhoneNumber);
            return myUserRepository.save(user);
        });
    }

    // 🔁 MISE À JOUR : email
    public Optional<User> updateEmail(Long userId, String newEmail) {
        return myUserRepository.findById(userId).map(user -> {
            user.setEmail(newEmail);
            return myUserRepository.save(user);
        });
    }
}
