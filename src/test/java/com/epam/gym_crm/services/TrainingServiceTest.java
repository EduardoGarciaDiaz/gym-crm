package com.epam.gym_crm.services;

import com.epam.gym_crm.dtos.TrainingDTO;
import com.epam.gym_crm.entities.Training;
import com.epam.gym_crm.repositories.TrainingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingRepository trainingRepository;

    private TrainingService service;

    @BeforeEach
    void setUp() {
        service = new TrainingService();
        service.setTrainingRepository(trainingRepository);
    }

    @Test
    void createTraining_mapsDtoToEntityAndSavesIt() {
        TrainingDTO dto = new TrainingDTO();
        dto.setTraineeId(1L);
        dto.setTrainerId(2L);
        dto.setTrainingName("Morning Yoga");
        dto.setTrainingType("Yoga");
        dto.setTrainingDate(LocalDate.of(2026, 11, 20));
        dto.setTrainingDuration(60);

        when(trainingRepository.saveTraining(any(Training.class))).thenAnswer(invocation -> {
            Training t = invocation.getArgument(0);
            t.setId(3L);
            return t;
        });

        Training result = service.createTraining(dto);

        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getTrainee()).isEqualTo(1L);
        assertThat(result.getTrainerId()).isEqualTo(2L);
        assertThat(result.getTrainingName()).isEqualTo("Morning Yoga");
        assertThat(result.getTrainingType().getName()).isEqualTo("Yoga");
        assertThat(result.getTrainingDate()).isEqualTo(LocalDate.of(2026, 11, 20));
        assertThat(result.getTrainingDuration()).isEqualTo(60);

        ArgumentCaptor<Training> captor = ArgumentCaptor.forClass(Training.class);
        verify(trainingRepository).saveTraining(captor.capture());
        assertThat(captor.getValue()).isSameAs(result);
    }

    @Test
    void getTrainingById_existingId_returnsTraining() {
        Training existing = new Training();
        existing.setId(1L);
        when(trainingRepository.findTrainingById(1L)).thenReturn(Optional.of(existing));

        assertThat(service.getTrainingById(1L)).containsSame(existing);
    }

    @Test
    void getTrainingById_unknownId_returnsEmpty() {
        when(trainingRepository.findTrainingById(99L)).thenReturn(Optional.empty());

        assertThat(service.getTrainingById(99L)).isEmpty();
    }
}
