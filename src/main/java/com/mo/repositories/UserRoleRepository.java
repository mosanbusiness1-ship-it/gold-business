package com.mo.repositories;

import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.mo.auth.User;

@Repository
public class UserRoleRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRoleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Ajouter un rôle pour un utilisateur dans la table user_roles
    public void addUserRole(Long userId, Long roleId) {
        String sql = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, roleId);
    }

	public Optional<User> findByName(String string) {
		// TODO Auto-generated method stub
		return null;
	}
}

