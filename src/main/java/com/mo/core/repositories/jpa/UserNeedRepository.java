package com.mo.core.repositories.jpa;

import com.mo.core.model.needs.AbstractUserNeed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNeedRepository extends JpaRepository<AbstractUserNeed, Long> {
    
    // Récupère tous les besoins d'un utilisateur spécifique
    List<AbstractUserNeed> findByUserId(Long userId);
}