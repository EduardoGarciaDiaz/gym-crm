package com.epam.gym_crm.services;

import com.epam.gym_crm.dtos.TrainingDTO;
import com.epam.gym_crm.entities.Training;
import com.epam.gym_crm.entities.TrainingType;
import com.epam.gym_crm.repositories.TrainingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TrainingService {

    private static final Logger log = LoggerFactory.getLogger(TrainingService.class);

    private TrainingRepository trainingRepository;

    @Autowired
    public void setTrainingRepository(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    public Training createTraining(TrainingDTO trainingDTO) {
        log.info("Creating training '{}' for traineeId={} with trainerId={}",
                trainingDTO.getTrainingName(), trainingDTO.getTraineeId(), trainingDTO.getTrainerId());
        TrainingType trainingType = new TrainingType(trainingDTO.getTrainingType());

        Training newTraining = new Training();
        newTraining.setTraineeId(trainingDTO.getTraineeId());
        newTraining.setTrainerId(trainingDTO.getTrainerId());
        newTraining.setTrainingName(trainingDTO.getTrainingName());
        newTraining.setTrainingType(trainingType);
        newTraining.setTrainingDate(trainingDTO.getTrainingDate());
        newTraining.setTrainingDuration(trainingDTO.getTrainingDuration());

        newTraining = trainingRepository.saveTraining(newTraining);
        log.info("Training created with id={}", newTraining.getId());
        return newTraining;
    }

    public Optional<Training> getTrainingById(Long id) {
        log.debug("Looking up training with id={}", id);
        Optional<Training> training = trainingRepository.findTrainingById(id);

        if (training.isEmpty()) {
            log.debug("Training with id={} not found", id);
        }
        return training;
    }
}