package com.epam.gym_crm.services;

import com.epam.gym_crm.dtos.Credentials;
import com.epam.gym_crm.entities.Trainee;
import com.epam.gym_crm.entities.Trainer;
import com.epam.gym_crm.exceptions.AuthenticationException;
import com.epam.gym_crm.repositories.ITraineeRepository;
import com.epam.gym_crm.repositories.ITrainerRepository;
import com.epam.gym_crm.repositories.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private ITraineeRepository traineeRepository;
    @Mock
    private ITrainerRepository trainerRepository;
    @Mock
    private IUserRepository userRepository;

    private AuthenticationService service;

    @BeforeEach
    void setUp() {
        service = new AuthenticationService();
        service.setTraineeRepository(traineeRepository);
        service.setTrainerRepository(trainerRepository);
        service.setUserRepository(userRepository);
    }

    private Trainee trainee(String username, String password) {
        Trainee t = new Trainee();
        t.setUsername(username);
        t.setPassword(password);
        return t;
    }

    private Trainer trainer(String username, String password) {
        Trainer t = new Trainer();
        t.setUsername(username);
        t.setPassword(password);
        return t;
    }

    @Test
    void matchesTrainee_correctCredentials_returnsTrue() {
        when(traineeRepository.findByUsername("John.Smith")).thenReturn(Optional.of(trainee("John.Smith", "secret12345")));

        assertThat(service.matchesTrainee(new Credentials("John.Smith", "secret12345"))).isTrue();
    }

    @Test
    void matchesTrainee_wrongPassword_returnsFalse() {
        when(traineeRepository.findByUsername("John.Smith")).thenReturn(Optional.of(trainee("John.Smith", "secret12345")));

        assertThat(service.matchesTrainee(new Credentials("John.Smith", "wrong"))).isFalse();
    }

    @Test
    void matchesTrainee_unknownUser_returnsFalse() {
        when(traineeRepository.findByUsername("No.Existe")).thenReturn(Optional.empty());

        assertThat(service.matchesTrainee(new Credentials("No.Existe", "x"))).isFalse();
    }

    @Test
    void matchesTrainee_nullOrBlankCredentials_returnsFalseWithoutQueryingDb() {
        assertThat(service.matchesTrainee(null)).isFalse();
        assertThat(service.matchesTrainee(new Credentials(null, "x"))).isFalse();
        assertThat(service.matchesTrainee(new Credentials("John.Smith", null))).isFalse();
        assertThat(service.matchesTrainee(new Credentials(" ", "x"))).isFalse();
        assertThat(service.matchesTrainee(new Credentials("John.Smith", ""))).isFalse();

        verifyNoInteractions(traineeRepository);
    }

    @Test
    void matchesTrainer_correctCredentials_returnsTrue() {
        when(trainerRepository.findByUsername("Ana.Torres")).thenReturn(Optional.of(trainer("Ana.Torres", "pw")));

        assertThat(service.matchesTrainer(new Credentials("Ana.Torres", "pw"))).isTrue();
    }

    @Test
    void matchesTrainer_traineeUsername_returnsFalse() {
        when(trainerRepository.findByUsername("John.Smith")).thenReturn(Optional.empty());

        assertThat(service.matchesTrainer(new Credentials("John.Smith", "secret12345"))).isFalse();
    }

    @Test
    void authenticateTrainee_valid_doesNotThrow() {
        when(traineeRepository.findByUsername("John.Smith")).thenReturn(Optional.of(trainee("John.Smith", "pw")));

        service.authenticateTrainee(new Credentials("John.Smith", "pw"));
    }

    @Test
    void authenticateTrainee_invalid_throwsGenericMessage() {
        when(traineeRepository.findByUsername("John.Smith")).thenReturn(Optional.of(trainee("John.Smith", "pw")));

        assertThatThrownBy(() -> service.authenticateTrainee(new Credentials("John.Smith", "bad")))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid username or password");
    }

    @Test
    void authenticateTrainer_unknownUser_throws() {
        when(trainerRepository.findByUsername("X.Y")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.authenticateTrainer(new Credentials("X.Y", "pw")))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void authenticateTrainer_nullCredentials_throws() {
        assertThatThrownBy(() -> service.authenticateTrainer(null))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void authenticateUser_validForAnyRole_doesNotThrow() {
        when(userRepository.findByUsername("Ana.Torres")).thenReturn(Optional.of(trainer("Ana.Torres", "pw")));

        service.authenticateUser(new Credentials("Ana.Torres", "pw"));
    }

    @Test
    void authenticateUser_wrongPassword_throws() {
        when(userRepository.findByUsername("Ana.Torres")).thenReturn(Optional.of(trainer("Ana.Torres", "pw")));

        assertThatThrownBy(() -> service.authenticateUser(new Credentials("Ana.Torres", "bad")))
                .isInstanceOf(AuthenticationException.class);
    }
}
