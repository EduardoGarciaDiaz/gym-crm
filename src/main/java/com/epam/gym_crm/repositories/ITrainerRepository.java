package com.epam.gym_crm.repositories;

import com.epam.gym_crm.entities.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITrainerRepository extends JpaRepository<Trainer, Long> {
    boolean existsByUsername(String username);
    Trainer findByUsernameIgnoreCase(String username);
}
