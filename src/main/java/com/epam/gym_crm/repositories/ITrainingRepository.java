package com.epam.gym_crm.repositories;

import com.epam.gym_crm.entities.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ITrainingRepository extends JpaRepository<Training, Long>, JpaSpecificationExecutor<Training> {
}
