package com.epam.gym_crm.facade;

import com.epam.gym_crm.dtos.TraineeDTO;
import com.epam.gym_crm.dtos.TrainerDTO;
import com.epam.gym_crm.dtos.TrainingDTO;
import com.epam.gym_crm.entities.Trainee;
import com.epam.gym_crm.entities.Trainer;
import com.epam.gym_crm.entities.Training;
import com.epam.gym_crm.services.TraineeService;
import com.epam.gym_crm.services.TrainerService;
import com.epam.gym_crm.services.TrainingService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GymFacade {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public GymFacade(TraineeService traineeService,
                     TrainerService trainerService,
                     TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    public Trainee createTrainee(TraineeDTO dto) {
        return traineeService.createTrainee(dto);
    }

    public boolean deleteTrainee(String username) {
        return traineeService.deleteTraineeByUsername(username);
    }

    public Trainee updateTrainee(Long id, TraineeDTO dto) {
        return traineeService.updateTrainee(id, dto);
    }

    public Trainee getTrainee(String username) {
        return traineeService.getTraineeByUsername(username);
    }

    public Trainer createTrainer(TrainerDTO dto) {
        return trainerService.createTrainer(dto);
    }

    public Trainer updateTrainer(Long id, TrainerDTO dto) {
        return trainerService.updateTrainer(id, dto);
    }

    public Trainer getTrainer(String username) {
        return trainerService.getTrainerByUsername(username);
    }

    public Training createTraining(TrainingDTO dto) {
        return trainingService.createTraining(dto);
    }

    public Training getTraining(String name) {
        return trainingService.getTrainingByTrainingName(name);
    }
}
