package com.mo.auth.totp;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTotpSecretRepository extends JpaRepository<UserTotpSecret, Long> {
	Optional<UserTotpSecret> findByUserId(Long userId);

}

