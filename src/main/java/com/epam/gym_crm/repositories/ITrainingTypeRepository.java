package com.epam.gym_crm.repositories;

import com.epam.gym_crm.entities.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ITrainingTypeRepository extends JpaRepository<TrainingType, Long> {
    Optional<TrainingType> findByNameIgnoreCase(String name);
}