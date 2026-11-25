package com.epam.gym_crm.services;

import com.epam.gym_crm.dtos.TraineeDTO;
import com.epam.gym_crm.entities.Trainee;
import com.epam.gym_crm.mappers.TraineeMapper;
import com.epam.gym_crm.repositories.ITraineeRepository;
import com.epam.gym_crm.repositories.IUserRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private ITraineeRepository traineeRepository;
    @Mock
    private IUserRepository userRepository;
    @Mock
    private CredentialsGenerator credentialsGenerator;
    @Mock
    private TraineeMapper traineeMapper;

    private TraineeService service;

    @BeforeEach
    void setUp() {
        service = new TraineeService();
        service.setTraineeRepository(traineeRepository);
        service.setUserRepository(userRepository);
        service.setCredentialsGenerator(credentialsGenerator);
        service.setTraineeMapper(traineeMapper);
    }

    private Trainee trainee(String username, String password, boolean active) {
        Trainee t = new Trainee();
        t.setUsername(username);
        t.setPassword(password);
        t.setIsActive(active);
        return t;
    }

    @Test
    void createTrainee_setsGeneratedCredentialsAndActive_andSaves() {
        TraineeDTO dto = new TraineeDTO();
        dto.setFirstName("John");
        dto.setLastName("Smith");
        when(credentialsGenerator.generateUsername(any(), any(), any())).thenReturn("John.Smith");
        when(credentialsGenerator.generatePassword()).thenReturn("abcde12345");
        when(traineeMapper.dtoToTrainee(dto)).thenReturn(new Trainee());
        when(traineeRepository.save(any(Trainee.class))).thenAnswer(inv -> inv.getArgument(0));

        Trainee created = service.createTrainee(dto);

        assertThat(created.getUsername()).isEqualTo("John.Smith");
        assertThat(created.getPassword()).isEqualTo("abcde12345");
        assertThat(created.getIsActive()).isTrue();
        verify(traineeRepository).save(created);
    }

    @Test
    void deleteTraineeByUsername_found_returnsTrue() {
        when(traineeRepository.deleteByUsername("John.Smith")).thenReturn(1L);

        assertThat(service.deleteTraineeByUsername("John.Smith")).isTrue();
    }

    @Test
    void deleteTraineeByUsername_notFound_returnsFalse() {
        when(traineeRepository.deleteByUsername("No.Existe")).thenReturn(0L);

        assertThat(service.deleteTraineeByUsername("No.Existe")).isFalse();
    }

    @Test
    void updateTrainee_existing_appliesDtoAndSaves() {
        Trainee existing = trainee("John.Smith", "pw", true);
        TraineeDTO dto = new TraineeDTO();
        when(traineeRepository.findByUsername("John.Smith")).thenReturn(Optional.of(existing));
        when(traineeRepository.save(existing)).thenReturn(existing);

        Trainee updated = service.updateTrainee("John.Smith", dto);

        assertThat(updated).isSameAs(existing);
        verify(traineeMapper).updateTraineeFromDTO(dto, existing);
    }

    @Test
    void updateTrainee_unknownUsername_throwsNoSuchElement() {
        when(traineeRepository.findByUsername("No.Existe")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateTrainee("No.Existe", new TraineeDTO()))
                .isInstanceOf(NoSuchElementException.class);
        verify(traineeRepository, never()).save(any());
    }

    @Test
    void getTraineeByUsername_delegatesToRepository() {
        Trainee t = trainee("John.Smith", "pw", true);
        when(traineeRepository.findByUsername("John.Smith")).thenReturn(Optional.of(t));

        assertThat(service.getTraineeByUsername("John.Smith")).containsSame(t);
        when(traineeRepository.findByUsername("No.Existe")).thenReturn(Optional.empty());
        assertThat(service.getTraineeByUsername("No.Existe")).isEmpty();
    }

    @Test
    void changePassword_correctOld_changesAndReturnsTrue() {
        Trainee t = trainee("John.Smith", "old", true);
        when(traineeRepository.findByUsername("John.Smith")).thenReturn(Optional.of(t));

        assertThat(service.changePassword("John.Smith", "old", "new")).isTrue();
        assertThat(t.getPassword()).isEqualTo("new");
    }

    @Test
    void changePassword_wrongOld_keepsPasswordAndReturnsFalse() {
        Trainee t = trainee("John.Smith", "old", true);
        when(traineeRepository.findByUsername("John.Smith")).thenReturn(Optional.of(t));

        assertThat(service.changePassword("John.Smith", "bad", "new")).isFalse();
        assertThat(t.getPassword()).isEqualTo("old");
    }

    @Test
    void changePassword_unknownUser_throwsNoSuchElement() {
        when(traineeRepository.findByUsername("No.Existe")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changePassword("No.Existe", "a", "b"))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void toggleStatus_flipsAndReturnsNewState() {
        Trainee t = trainee("John.Smith", "pw", true);
        when(traineeRepository.findByUsername("John.Smith")).thenReturn(Optional.of(t));

        assertThat(service.toggleStatus("John.Smith")).isFalse();
        assertThat(service.toggleStatus("John.Smith")).isTrue();
    }

    @Test
    void toggleStatus_unknownUser_throwsNoSuchElement() {
        when(traineeRepository.findByUsername("No.Existe")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.toggleStatus("No.Existe")).isInstanceOf(NoSuchElementException.class);
    }
}
