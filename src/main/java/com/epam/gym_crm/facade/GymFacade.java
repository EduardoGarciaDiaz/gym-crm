package com.epam.gym_crm.facade;

import com.epam.gym_crm.dtos.Credentials;
import com.epam.gym_crm.dtos.TraineeDTO;
import com.epam.gym_crm.dtos.TraineeTrainingCriteria;
import com.epam.gym_crm.dtos.TrainerDTO;
import com.epam.gym_crm.dtos.TrainerTrainingCriteria;
import com.epam.gym_crm.dtos.TrainingDTO;
import com.epam.gym_crm.entities.Trainee;
import com.epam.gym_crm.entities.Trainer;
import com.epam.gym_crm.entities.Training;
import com.epam.gym_crm.services.AuthenticationService;
import com.epam.gym_crm.services.TraineeService;
import com.epam.gym_crm.services.TrainerService;
import com.epam.gym_crm.services.TrainingService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GymFacade {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final AuthenticationService authenticationService;

    public GymFacade(TraineeService traineeService,
                     TrainerService trainerService,
                     TrainingService trainingService,
                     AuthenticationService authenticationService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.authenticationService = authenticationService;
    }

    public boolean matchTraineeCredentials(Credentials auth) {
        return authenticationService.matchesTrainee(auth);
    }

    public boolean matchTrainerCredentials(Credentials auth) {
        return authenticationService.matchesTrainer(auth);
    }

    public Trainee createTrainee(TraineeDTO dto) {
        return traineeService.createTrainee(dto);
    }

    public Optional<Trainee> getTrainee(Credentials auth, String username) {
        authenticationService.authenticateTrainee(auth);
        return traineeService.getTraineeByUsername(username);
    }

    public Trainee updateTrainee(Credentials auth, String username, TraineeDTO dto) {
        authenticationService.authenticateTrainee(auth);
        requireSelf(auth, username);
        return traineeService.updateTrainee(username, dto);
    }

    public boolean deleteTrainee(Credentials auth, String username) {
        authenticationService.authenticateTrainee(auth);
        requireSelf(auth, username);
        return traineeService.deleteTraineeByUsername(username);
    }

    public boolean changeTraineePassword(Credentials auth, String newPassword) {
        authenticationService.authenticateTrainee(auth);
        return traineeService.changePassword(auth.username(), auth.password(), newPassword);
    }

    public boolean toggleTraineeStatus(Credentials auth) {
        authenticationService.authenticateTrainee(auth);
        return traineeService.toggleStatus(auth.username());
    }

    public List<Trainer> getUnassignedTrainers(Credentials auth) {
        authenticationService.authenticateTrainee(auth);
        return traineeService.getUnassignedTrainers(auth.username());
    }

    public List<Training> getTraineeTrainings(Credentials auth, TraineeTrainingCriteria criteria) {
        authenticationService.authenticateTrainee(auth);
        return trainingService.getTraineeTrainings(auth.username(), criteria);
    }

    public Trainer createTrainer(TrainerDTO dto) {
        return trainerService.createTrainer(dto);
    }

    public Optional<Trainer> getTrainer(Credentials auth, String username) {
        authenticationService.authenticateTrainer(auth);
        return trainerService.getTrainerByUsername(username);
    }

    public Trainer updateTrainer(Credentials auth, String username, TrainerDTO dto) {
        authenticationService.authenticateTrainer(auth);
        requireSelf(auth, username);
        return trainerService.updateTrainer(username, dto);
    }

    public boolean changeTrainerPassword(Credentials auth, String newPassword) {
        authenticationService.authenticateTrainer(auth);
        return trainerService.changePassword(auth.username(), auth.password(), newPassword);
    }

    public boolean toggleTrainerStatus(Credentials auth) {
        authenticationService.authenticateTrainer(auth);
        return trainerService.toggleStatus(auth.username());
    }

    public List<Training> getTrainerTrainings(Credentials auth, TrainerTrainingCriteria criteria) {
        authenticationService.authenticateTrainer(auth);
        return trainingService.getTrainerTrainings(auth.username(), criteria);
    }

    public Training createTraining(Credentials auth, TrainingDTO dto) {
        authenticationService.authenticateUser(auth);
        return trainingService.createTraining(dto);
    }

    private void requireSelf(Credentials auth, String targetUsername) {
        if (!auth.username().equals(targetUsername)) {
            throw new SecurityException("A user can only modify their own account");
        }
    }
}
