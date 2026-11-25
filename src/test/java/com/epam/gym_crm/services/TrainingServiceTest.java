package com.epam.gym_crm.services;

import com.epam.gym_crm.dtos.TrainingDTO;
import com.epam.gym_crm.entities.Trainee;
import com.epam.gym_crm.entities.Trainer;
import com.epam.gym_crm.entities.Training;
import com.epam.gym_crm.entities.TrainingType;
import com.epam.gym_crm.repositories.ITraineeRepository;
import com.epam.gym_crm.repositories.ITrainerRepository;
import com.epam.gym_crm.repositories.ITrainingRepository;
import com.epam.gym_crm.repositories.ITrainingTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private ITrainingRepository trainingRepository;
    @Mock
    private ITraineeRepository traineeRepository;
    @Mock
    private ITrainerRepository trainerRepository;
    @Mock
    private ITrainingTypeRepository trainingTypeRepository;

    private TrainingService service;
    private final TrainingDTO dto =
            new TrainingDTO(1L, 2L, "Morning Yoga", "Yoga", LocalDate.of(2026, 12, 1), 60);

    @BeforeEach
    void setUp() {
        service = new TrainingService();
        service.setTrainingRepository(trainingRepository);
        service.setTraineeRepository(traineeRepository);
        service.setTrainerRepository(trainerRepository);
        service.setTrainingTypeRepository(trainingTypeRepository);
    }

    @Test
    void createTraining_allReferencesExist_buildsAndSavesTraining() {
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        TrainingType yoga = new TrainingType("Yoga");
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(2L)).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByNameIgnoreCase("Yoga")).thenReturn(Optional.of(yoga));
        when(trainingRepository.save(any(Training.class))).thenAnswer(inv -> inv.getArgument(0));

        Training created = service.createTraining(dto);

        assertThat(created.getTrainee()).isSameAs(trainee);
        assertThat(created.getTrainer()).isSameAs(trainer);
        assertThat(created.getTrainingType()).isSameAs(yoga);
        assertThat(created.getTrainingName()).isEqualTo("Morning Yoga");
        assertThat(created.getTrainingDate()).isEqualTo(LocalDate.of(2026, 12, 1));
        assertThat(created.getTrainingDuration()).isEqualTo(60);
    }

    @Test
    void createTraining_unknownTrainee_throwsAndDoesNotSave() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createTraining(dto)).isInstanceOf(NoSuchElementException.class);
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void createTraining_unknownTrainer_throws() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(new Trainee()));
        when(trainerRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createTraining(dto)).isInstanceOf(NoSuchElementException.class);
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void createTraining_unknownType_throws() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(new Trainee()));
        when(trainerRepository.findById(2L)).thenReturn(Optional.of(new Trainer()));
        when(trainingTypeRepository.findByNameIgnoreCase("Yoga")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createTraining(dto)).isInstanceOf(NoSuchElementException.class);
        verify(trainingRepository, never()).save(any());
    }
}
