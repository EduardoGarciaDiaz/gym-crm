package com.epam.gym_crm.services;

import com.epam.gym_crm.dtos.TrainingDTO;
import com.epam.gym_crm.entities.Training;
import com.epam.gym_crm.entities.TrainingType;
import com.epam.gym_crm.repositories.TrainingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TrainingService {
    private TrainingRepository trainingRepository;

    @Autowired
    public void setTrainingRepository(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    public Training createTraining(TrainingDTO trainingDTO) {
        TrainingType trainingType = new TrainingType(trainingDTO.getTrainingType());
        Training newTraining = new Training(
                trainingDTO.getTraineeId(),
                trainingDTO.getTrainerId(),
                trainingDTO.getTrainingName(),
                trainingType,
                trainingDTO.getTrainingDate(),
                trainingDTO.getTrainingDuration()
        );

        newTraining = trainingRepository.saveTraining(newTraining);
        return newTraining;
    }

    public Optional<Training> getTrainingById(Long id) {
        return trainingRepository.findTrainingById(id);
    }
}
