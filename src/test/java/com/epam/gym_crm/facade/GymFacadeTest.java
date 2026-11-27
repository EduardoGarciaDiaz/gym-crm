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
import com.epam.gym_crm.exceptions.AuthenticationException;
import com.epam.gym_crm.services.AuthenticationService;
import com.epam.gym_crm.services.TraineeService;
import com.epam.gym_crm.services.TrainerService;
import com.epam.gym_crm.services.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TrainingService trainingService;
    @Mock
    private AuthenticationService authenticationService;

    private GymFacade facade;
    private final Credentials john = new Credentials("John.Smith", "pw");
    private final Credentials ana = new Credentials("Ana.Torres", "pw");

    @BeforeEach
    void setUp() {
        facade = new GymFacade(traineeService, trainerService, trainingService, authenticationService);
    }

    // ---- creación: sin credenciales ----

    @Test
    void createTrainee_needsNoAuthentication() {
        TraineeDTO dto = new TraineeDTO();
        Trainee created = new Trainee();
        when(traineeService.createTrainee(dto)).thenReturn(created);

        assertThat(facade.createTrainee(dto)).isSameAs(created);
        verifyNoInteractions(authenticationService);
    }

    @Test
    void createTrainer_needsNoAuthentication() {
        TrainerDTO dto = new TrainerDTO();
        Trainer created = new Trainer();
        when(trainerService.createTrainer(dto)).thenReturn(created);

        assertThat(facade.createTrainer(dto)).isSameAs(created);
        verifyNoInteractions(authenticationService);
    }

    // ---- match*Credentials ----

    @Test
    void matchTraineeCredentials_delegatesToAuthService() {
        when(authenticationService.matchesTrainee(john)).thenReturn(true);

        assertThat(facade.matchTraineeCredentials(john)).isTrue();
    }

    @Test
    void matchTrainerCredentials_delegatesToAuthService() {
        when(authenticationService.matchesTrainer(ana)).thenReturn(false);

        assertThat(facade.matchTrainerCredentials(ana)).isFalse();
    }

    // ---- operaciones autenticadas ----

    @Test
    void getTrainee_authenticatesThenDelegates() {
        Trainee t = new Trainee();
        when(traineeService.getTraineeByUsername("John.Smith")).thenReturn(Optional.of(t));

        assertThat(facade.getTrainee(john, "John.Smith")).containsSame(t);
        verify(authenticationService).authenticateTrainee(john);
    }

    @Test
    void getTrainee_badCredentials_throwsAndDoesNotTouchService() {
        doThrow(new AuthenticationException("Invalid username or password"))
                .when(authenticationService).authenticateTrainee(john);

        assertThatThrownBy(() -> facade.getTrainee(john, "John.Smith"))
                .isInstanceOf(AuthenticationException.class);
        verifyNoInteractions(traineeService);
    }

    @Test
    void getTrainer_authenticatesAsTrainerThenDelegates() {
        Trainer t = new Trainer();
        when(trainerService.getTrainerByUsername("Ana.Torres")).thenReturn(Optional.of(t));

        assertThat(facade.getTrainer(ana, "Ana.Torres")).containsSame(t);
        verify(authenticationService).authenticateTrainer(ana);
    }

    @Test
    void updateTrainee_ownAccount_delegates() {
        TraineeDTO dto = new TraineeDTO();
        Trainee updated = new Trainee();
        when(traineeService.updateTrainee("John.Smith", dto)).thenReturn(updated);

        assertThat(facade.updateTrainee(john, "John.Smith", dto)).isSameAs(updated);
        verify(authenticationService).authenticateTrainee(john);
    }

    @Test
    void updateTrainee_otherAccount_throwsSecurityException() {
        assertThatThrownBy(() -> facade.updateTrainee(john, "Other.User", new TraineeDTO()))
                .isInstanceOf(SecurityException.class);
        verifyNoInteractions(traineeService);
    }

    @Test
    void deleteTrainee_ownAccount_delegates() {
        when(traineeService.deleteTraineeByUsername("John.Smith")).thenReturn(true);

        assertThat(facade.deleteTrainee(john, "John.Smith")).isTrue();
        verify(authenticationService).authenticateTrainee(john);
    }

    @Test
    void deleteTrainee_otherAccount_throwsSecurityException() {
        assertThatThrownBy(() -> facade.deleteTrainee(john, "Other.User"))
                .isInstanceOf(SecurityException.class);
        verifyNoInteractions(traineeService);
    }

    @Test
    void updateTrainer_ownAccount_delegates() {
        TrainerDTO dto = new TrainerDTO();
        Trainer updated = new Trainer();
        when(trainerService.updateTrainer("Ana.Torres", dto)).thenReturn(updated);

        assertThat(facade.updateTrainer(ana, "Ana.Torres", dto)).isSameAs(updated);
        verify(authenticationService).authenticateTrainer(ana);
    }

    @Test
    void updateTrainer_otherAccount_throwsSecurityException() {
        assertThatThrownBy(() -> facade.updateTrainer(ana, "Other.User", new TrainerDTO()))
                .isInstanceOf(SecurityException.class);
        verifyNoInteractions(trainerService);
    }

    @Test
    void changeTraineePassword_usesCredentialsAsOldPassword() {
        when(traineeService.changePassword("John.Smith", "pw", "new")).thenReturn(true);

        assertThat(facade.changeTraineePassword(john, "new")).isTrue();
        verify(authenticationService).authenticateTrainee(john);
    }

    @Test
    void changeTrainerPassword_usesCredentialsAsOldPassword() {
        when(trainerService.changePassword("Ana.Torres", "pw", "new")).thenReturn(true);

        assertThat(facade.changeTrainerPassword(ana, "new")).isTrue();
        verify(authenticationService).authenticateTrainer(ana);
    }

    @Test
    void toggleTraineeStatus_authenticatesAndTogglesOwnAccount() {
        when(traineeService.toggleStatus("John.Smith")).thenReturn(false);

        assertThat(facade.toggleTraineeStatus(john)).isFalse();
        verify(authenticationService).authenticateTrainee(john);
    }

    @Test
    void toggleTrainerStatus_authenticatesAndTogglesOwnAccount() {
        when(trainerService.toggleStatus("Ana.Torres")).thenReturn(true);

        assertThat(facade.toggleTrainerStatus(ana)).isTrue();
        verify(authenticationService).authenticateTrainer(ana);
    }

    @Test
    void toggleTraineeStatus_badCredentials_neverReachesService() {
        doThrow(new AuthenticationException("Invalid username or password"))
                .when(authenticationService).authenticateTrainee(john);

        assertThatThrownBy(() -> facade.toggleTraineeStatus(john)).isInstanceOf(AuthenticationException.class);
        verifyNoInteractions(traineeService);
    }

    @Test
    void createTraining_authenticatesAnyUserThenDelegates() {
        TrainingDTO dto = new TrainingDTO();
        Training created = new Training();
        when(trainingService.createTraining(dto)).thenReturn(created);

        assertThat(facade.createTraining(ana, dto)).isSameAs(created);
        verify(authenticationService).authenticateUser(ana);
    }

    @Test
    void createTraining_badCredentials_neverReachesService() {
        doThrow(new AuthenticationException("Invalid username or password"))
                .when(authenticationService).authenticateUser(ana);

        assertThatThrownBy(() -> facade.createTraining(ana, new TrainingDTO()))
                .isInstanceOf(AuthenticationException.class);
        verifyNoInteractions(trainingService);
    }

    // ---- req. 14, 15, 17, 18 ----

    @Test
    void getUnassignedTrainers_usesOwnUsername() {
        List<Trainer> trainers = List.of(new Trainer());
        when(traineeService.getUnassignedTrainers("John.Smith")).thenReturn(trainers);

        assertThat(facade.getUnassignedTrainers(john)).isSameAs(trainers);
        verify(authenticationService).authenticateTrainee(john);
    }

    @Test
    void getTraineeTrainings_usesOwnUsernameAndPassesCriteria() {
        TraineeTrainingCriteria criteria = new TraineeTrainingCriteria(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), "ana", "Yoga");
        List<Training> trainings = List.of(new Training());
        when(trainingService.getTraineeTrainings("John.Smith", criteria)).thenReturn(trainings);

        assertThat(facade.getTraineeTrainings(john, criteria)).isSameAs(trainings);
        verify(authenticationService).authenticateTrainee(john);
    }

    @Test
    void getTrainerTrainings_usesOwnUsernameAndPassesCriteria() {
        TrainerTrainingCriteria criteria = new TrainerTrainingCriteria(null, null, "john");
        List<Training> trainings = List.of(new Training());
        when(trainingService.getTrainerTrainings("Ana.Torres", criteria)).thenReturn(trainings);

        assertThat(facade.getTrainerTrainings(ana, criteria)).isSameAs(trainings);
        verify(authenticationService).authenticateTrainer(ana);
    }

    @Test
    void getTraineeTrainings_badCredentials_neverReachesService() {
        doThrow(new AuthenticationException("Invalid username or password"))
                .when(authenticationService).authenticateTrainee(john);

        assertThatThrownBy(() -> facade.getTraineeTrainings(john, TraineeTrainingCriteria.none()))
                .isInstanceOf(AuthenticationException.class);
        verifyNoInteractions(trainingService);
    }
}
