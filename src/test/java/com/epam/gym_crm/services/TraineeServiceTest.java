package com.epam.gym_crm.services;

import com.epam.gym_crm.dtos.TraineeDTO;
import com.epam.gym_crm.entities.Trainee;
import com.epam.gym_crm.repositories.TraineeRepository;
import com.epam.gym_crm.utils.CredentialsGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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
class TraineeServiceTest {

    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private CredentialsGenerator credentialsGenerator;

    private TraineeService service;

    @BeforeEach
    void setUp() {
        service = new TraineeService();
        service.setTraineeRepository(traineeRepository);
        service.setCredentialsGenerator(credentialsGenerator);
    }

    private TraineeDTO dto(String firstName, String lastName) {
        TraineeDTO dto = new TraineeDTO();
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setDateOfBirth(LocalDate.of(2000, 1, 15));
        dto.setAddress("Calle Falsa 123");
        return dto;
    }

    // ---------- create ----------

    @Test
    void createTrainee_buildsActiveTraineeWithGeneratedCredentialsAndSavesIt() {
        when(credentialsGenerator.generateUsername(eq("John"), eq("Smith"), any())).thenReturn("John.Smith1");
        when(credentialsGenerator.generatePassword()).thenReturn("aB3dE5gH7j");
        when(traineeRepository.saveTrainee(any(Trainee.class))).thenAnswer(invocation -> {
            Trainee t = invocation.getArgument(0);
            t.setUserId(7L);
            return t;
        });

        Trainee result = service.createTrainee(dto("John", "Smith"));

        assertThat(result.getUserId()).isEqualTo(7L);
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getUsername()).isEqualTo("John.Smith1");
        assertThat(result.getPassword()).isEqualTo("aB3dE5gH7j");
        assertThat(result.getIsActive()).isTrue();
        assertThat(result.getDateOfBirth()).isEqualTo(LocalDate.of(2000, 1, 15));
        assertThat(result.getAddress()).isEqualTo("Calle Falsa 123");

        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);
        verify(traineeRepository).saveTrainee(captor.capture());
        assertThat(captor.getValue()).isSameAs(result);
    }

    // ---------- update ----------

    @Test
    void updateTrainee_existingTrainee_updatesFieldsAndKeepsCredentials() {
        Trainee existing = new Trainee("John", "Smith", "John.Smith", "pw12345678", true,
                LocalDate.of(1995, 3, 12), "Old address", 1L);
        when(traineeRepository.findTraineeById(1L)).thenReturn(Optional.of(existing));
        when(traineeRepository.saveTrainee(existing)).thenReturn(existing);

        TraineeDTO update = dto("Johnny", "Smithson");
        update.setAddress("New address");
        update.setDateOfBirth(LocalDate.of(1996, 4, 1));

        Trainee result = service.updateTrainee(1L, update);

        assertThat(result.getFirstName()).isEqualTo("Johnny");
        assertThat(result.getLastName()).isEqualTo("Smithson");
        assertThat(result.getAddress()).isEqualTo("New address");
        assertThat(result.getDateOfBirth()).isEqualTo(LocalDate.of(1996, 4, 1));
        // el username y el password se asignan solo al crear
        assertThat(result.getUsername()).isEqualTo("John.Smith");
        assertThat(result.getPassword()).isEqualTo("pw12345678");
        assertThat(result.getUserId()).isEqualTo(1L);
        verify(traineeRepository).saveTrainee(existing);
    }

    @Test
    void updateTrainee_unknownId_throwsNoSuchElementAndDoesNotSave() {
        when(traineeRepository.findTraineeById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateTrainee(99L, dto("John", "Smith")))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("99");

        verify(traineeRepository, never()).saveTrainee(any());
    }

    // ---------- delete ----------

    @Test
    void deleteTrainee_existingId_returnsTrue() {
        when(traineeRepository.deleteTraineeById(1L)).thenReturn(true);

        assertThat(service.deleteTrainee(1L)).isTrue();
    }

    @Test
    void deleteTrainee_unknownId_returnsFalse() {
        when(traineeRepository.deleteTraineeById(99L)).thenReturn(false);

        assertThat(service.deleteTrainee(99L)).isFalse();
    }

    // ---------- select ----------

    @Test
    void getTraineeById_existingId_returnsTrainee() {
        Trainee existing = new Trainee();
        existing.setUserId(1L);
        when(traineeRepository.findTraineeById(1L)).thenReturn(Optional.of(existing));

        assertThat(service.getTraineeById(1L)).containsSame(existing);
    }

    @Test
    void getTraineeById_unknownId_returnsEmpty() {
        when(traineeRepository.findTraineeById(99L)).thenReturn(Optional.empty());

        assertThat(service.getTraineeById(99L)).isEmpty();
    }
}
