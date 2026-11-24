package com.epam.gym_crm.repositories;

import com.epam.gym_crm.entities.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ITraineeRepository extends JpaRepository<Trainee, Long> {
    boolean existsByUsername(String username);
    Optional<Trainee> findByUsername(String username);
    long deleteByUsername(String username);
}
