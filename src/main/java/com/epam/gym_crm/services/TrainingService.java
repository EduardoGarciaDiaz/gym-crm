package com.epam.gym_crm.services;

import com.epam.gym_crm.dtos.TrainingDTO;
import com.epam.gym_crm.entities.Trainee;
import com.epam.gym_crm.entities.Trainer;
import com.epam.gym_crm.entities.Training;
import com.epam.gym_crm.entities.TrainingType;
import com.epam.gym_crm.repositories.ITrainingRepository;
import com.epam.gym_crm.repositories.TrainingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TrainingService {

    private static final Logger log = LoggerFactory.getLogger(TrainingService.class);

    private ITrainingRepository trainingRepository;

    @Autowired
    public void setTrainingRepository(ITrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    public Training createTraining(TrainingDTO trainingDTO) {
        log.info("Creating training '{}' for traineeId={} with trainerId={}",
                trainingDTO.getTrainingName(), trainingDTO.getTraineeId(), trainingDTO.getTrainerId());

        TrainingType trainingType = new TrainingType(trainingDTO.getTrainingType());
        Trainee trainee = new Trainee();
        trainee.setId(trainingDTO.getTraineeId());
        Trainer trainer = new Trainer();
        trainer.setId(trainingDTO.getTrainerId());


        Training newTraining = new Training();
        newTraining.setTrainee(trainee);
        newTraining.setTrainerId(trainer);
        newTraining.setTrainingName(trainingDTO.getTrainingName());
        newTraining.setTrainingType(trainingType);
        newTraining.setTrainingDate(trainingDTO.getTrainingDate());
        newTraining.setTrainingDuration(trainingDTO.getTrainingDuration());

        newTraining = trainingRepository.save(newTraining);
        log.info("Training created with id={}", newTraining.getId());
        return newTraining;
    }

    public Training getTrainingByTrainingName(String name) {
        log.debug("Looking up training with username={}", name);
        Training training = trainingRepository.findByTrainingNameIgnoreCase(name);

        if (training == null) {
            log.debug("Training with name={} not found", name);
        }
        return training;
    }
}