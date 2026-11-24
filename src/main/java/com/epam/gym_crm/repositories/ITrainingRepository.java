package com.epam.gym_crm.repositories;

import com.epam.gym_crm.entities.Trainee;
import com.epam.gym_crm.entities.Training;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITrainingRepository extends JpaRepository<Training, Long> {
}
