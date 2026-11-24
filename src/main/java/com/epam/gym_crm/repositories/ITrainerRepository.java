package com.epam.gym_crm.repositories;

import com.epam.gym_crm.entities.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ITrainerRepository extends JpaRepository<Trainer, Long> {
    boolean existsByUsername(String username);
    Optional<Trainer> findByUsername(String username);
}
