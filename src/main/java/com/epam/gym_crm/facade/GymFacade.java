package com.epam.gym_crm.facade;

import com.epam.gym_crm.dtos.TraineeDTO;
import com.epam.gym_crm.entities.Trainee;
import com.epam.gym_crm.services.TraineeService;
import com.epam.gym_crm.services.TrainerService;
import com.epam.gym_crm.services.TrainingService;
import org.springframework.stereotype.Component;

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

    public boolean deleteTrainee(Long traineeId) {
        return traineeService.deleteTrainee(traineeId);
    }

    public Trainee updateTrainee(Long id, TraineeDTO dto) {
        return traineeService.updateTrainee(id, dto);
    }

    public Trainee getTrainee(Long id) {
        return traineeService.getTraineeById(id);
    }
}
