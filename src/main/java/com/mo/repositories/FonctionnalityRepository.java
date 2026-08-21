package com.mo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mo.auth.Fonctionnality;

@Repository
public interface FonctionnalityRepository extends JpaRepository<Fonctionnality, Long> {
    Optional<Fonctionnality> findByName(String name);
}
