package com.epam.gym_crm.repositories;

import com.epam.gym_crm.entities.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITraineeRepository extends JpaRepository<Trainee, Long> {
    boolean existsByUsername(String username);
    Trainee findByUsernameIgnoreCase(String username);
    void deleteByUsername(String username);
}
