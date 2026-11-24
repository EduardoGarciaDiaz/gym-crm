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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TrainingService trainingService;

    private GymFacade facade;

    @BeforeEach
    void setUp() {
        facade = new GymFacade(traineeService, trainerService, trainingService);
    }

    @Test
    void createTrainee_delegatesToTraineeService() {
        TraineeDTO dto = new TraineeDTO();
        Trainee created = new Trainee();
        when(traineeService.createTrainee(dto)).thenReturn(created);

        assertThat(facade.createTrainee(dto)).isSameAs(created);
    }

    @Test
    void updateTrainee_delegatesToTraineeService() {
        TraineeDTO dto = new TraineeDTO();
        Trainee updated = new Trainee();
        when(traineeService.updateTrainee(1L, dto)).thenReturn(updated);

        assertThat(facade.updateTrainee(1L, dto)).isSameAs(updated);
    }

    @Test
    void deleteTrainee_delegatesToTraineeService() {
        when(traineeService.deleteTrainee(1L)).thenReturn(true);

        assertThat(facade.deleteTrainee(1L)).isTrue();
    }

    @Test
    void getTrainee_delegatesToTraineeService() {
        Trainee trainee = new Trainee();
        when(traineeService.getTraineeById(1L)).thenReturn(Optional.of(trainee));

        assertThat(facade.getTrainee(1L)).containsSame(trainee);
    }

    @Test
    void createTrainer_delegatesToTrainerService() {
        TrainerDTO dto = new TrainerDTO();
        Trainer created = new Trainer();
        when(trainerService.createTrainer(dto)).thenReturn(created);

        assertThat(facade.createTrainer(dto)).isSameAs(created);
    }

    @Test
    void updateTrainer_delegatesToTrainerService() {
        TrainerDTO dto = new TrainerDTO();
        Trainer updated = new Trainer();
        when(trainerService.updateTrainer(1L, dto)).thenReturn(updated);

        assertThat(facade.updateTrainer(1L, dto)).isSameAs(updated);
    }

    @Test
    void getTrainer_delegatesToTrainerService() {
        Trainer trainer = new Trainer();
        when(trainerService.getTrainerById(1L)).thenReturn(Optional.of(trainer));

        assertThat(facade.getTrainer(1L)).containsSame(trainer);
    }

    @Test
    void createTraining_delegatesToTrainingService() {
        TrainingDTO dto = new TrainingDTO();
        Training created = new Training();
        when(trainingService.createTraining(dto)).thenReturn(created);

        assertThat(facade.createTraining(dto)).isSameAs(created);
    }

    @Test
    void getTraining_delegatesToTrainingService() {
        Training training = new Training();
        when(trainingService.getTrainingById(1L)).thenReturn(Optional.of(training));

        assertThat(facade.getTraining(1L)).containsSame(training);
    }
}
