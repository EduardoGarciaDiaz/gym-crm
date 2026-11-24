package com.epam.gym_crm.services;

import com.epam.gym_crm.dtos.TrainerDTO;
import com.epam.gym_crm.entities.Trainer;
import com.epam.gym_crm.entities.TrainingType;
import com.epam.gym_crm.repositories.TrainerRepository;
import com.epam.gym_crm.utils.CredentialsGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private CredentialsGenerator credentialsGenerator;

    private TrainerService service;

    @BeforeEach
    void setUp() {
        service = new TrainerService();
        service.setTrainerRepository(trainerRepository);
        service.setCredentialsGenerator(credentialsGenerator);
    }

    private TrainerDTO dto(String firstName, String lastName, String specialization) {
        TrainerDTO dto = new TrainerDTO();
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setSpecialization(specialization);
        return dto;
    }

    // ---------- create ----------

    @Test
    void createTrainer_buildsActiveTrainerWithGeneratedCredentialsAndSavesIt() {
        when(credentialsGenerator.generateUsername(eq("Ana"), eq("Torres"), any())).thenReturn("Ana.Torres");
        when(credentialsGenerator.generatePassword()).thenReturn("Qw8Er5Ty2U");
        when(trainerRepository.saveTrainer(any(Trainer.class))).thenAnswer(invocation -> {
            Trainer t = invocation.getArgument(0);
            t.setUserId(5L);
            return t;
        });

        Trainer result = service.createTrainer(dto("Ana", "Torres", "Yoga"));

        assertThat(result.getUserId()).isEqualTo(5L);
        assertThat(result.getFirstName()).isEqualTo("Ana");
        assertThat(result.getLastName()).isEqualTo("Torres");
        assertThat(result.getUsername()).isEqualTo("Ana.Torres");
        assertThat(result.getPassword()).isEqualTo("Qw8Er5Ty2U");
        assertThat(result.getIsActive()).isTrue();
        assertThat(result.getSpecialization().getName()).isEqualTo("Yoga");
        verify(trainerRepository).saveTrainer(result);
    }

    // ---------- update ----------

    @Test
    void updateTrainer_existingTrainer_updatesFieldsAndKeepsCredentials() {
        Trainer existing = new Trainer("Ana", "Torres", "Ana.Torres", "pw12345678", true,
                new TrainingType("Yoga"), 1L);
        when(trainerRepository.findTrainerById(1L)).thenReturn(Optional.of(existing));
        when(trainerRepository.saveTrainer(existing)).thenReturn(existing);

        Trainer result = service.updateTrainer(1L, dto("Anita", "Torres Ruiz", "Pilates"));

        assertThat(result.getFirstName()).isEqualTo("Anita");
        assertThat(result.getLastName()).isEqualTo("Torres Ruiz");
        assertThat(result.getSpecialization().getName()).isEqualTo("Pilates");
        assertThat(result.getUsername()).isEqualTo("Ana.Torres");
        assertThat(result.getPassword()).isEqualTo("pw12345678");
        assertThat(result.getUserId()).isEqualTo(1L);
        verify(trainerRepository).saveTrainer(existing);
    }

    @Test
    void updateTrainer_unknownId_throwsNoSuchElementAndDoesNotSave() {
        when(trainerRepository.findTrainerById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateTrainer(99L, dto("Ana", "Torres", "Yoga")))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("99");

        verify(trainerRepository, never()).saveTrainer(any());
    }

    // ---------- select ----------

    @Test
    void getTrainerById_existingId_returnsTrainer() {
        Trainer existing = new Trainer();
        existing.setUserId(1L);
        when(trainerRepository.findTrainerById(1L)).thenReturn(Optional.of(existing));

        assertThat(service.getTrainerById(1L)).containsSame(existing);
    }

    @Test
    void getTrainerById_unknownId_returnsEmpty() {
        when(trainerRepository.findTrainerById(99L)).thenReturn(Optional.empty());

        assertThat(service.getTrainerById(99L)).isEmpty();
    }
}
