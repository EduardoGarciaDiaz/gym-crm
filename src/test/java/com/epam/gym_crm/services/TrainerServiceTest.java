package com.epam.gym_crm.services;

import com.epam.gym_crm.dtos.TrainerDTO;
import com.epam.gym_crm.entities.Trainer;
import com.epam.gym_crm.entities.TrainingType;
import com.epam.gym_crm.mappers.TrainerMapper;
import com.epam.gym_crm.repositories.ITrainerRepository;
import com.epam.gym_crm.repositories.ITrainingTypeRepository;
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
class TrainerServiceTest {

    @Mock
    private ITrainerRepository trainerRepository;
    @Mock
    private IUserRepository userRepository;
    @Mock
    private ITrainingTypeRepository trainingTypeRepository;
    @Mock
    private CredentialsGenerator credentialsGenerator;
    @Mock
    private TrainerMapper trainerMapper;

    private TrainerService service;

    @BeforeEach
    void setUp() {
        service = new TrainerService();
        service.setTrainerRepository(trainerRepository);
        service.setUserRepository(userRepository);
        service.setTrainingTypeRepository(trainingTypeRepository);
        service.setCredentialsGenerator(credentialsGenerator);
        service.setTrainerMapper(trainerMapper);
    }

    private TrainerDTO dto(String specialization) {
        TrainerDTO dto = new TrainerDTO();
        dto.setFirstName("Ana");
        dto.setLastName("Torres");
        dto.setSpecialization(specialization);
        return dto;
    }

    private Trainer trainer(String username, String password, boolean active) {
        Trainer t = new Trainer();
        t.setUsername(username);
        t.setPassword(password);
        t.setIsActive(active);
        return t;
    }

    @Test
    void createTrainer_validType_setsTypeCredentialsAndActive() {
        TrainerDTO dto = dto("Yoga");
        TrainingType yoga = new TrainingType("Yoga");
        when(credentialsGenerator.generateUsername(any(), any(), any())).thenReturn("Ana.Torres");
        when(trainingTypeRepository.findByNameIgnoreCase("Yoga")).thenReturn(Optional.of(yoga));
        when(trainerMapper.dtoToTrainer(dto)).thenReturn(new Trainer());
        when(credentialsGenerator.generatePassword()).thenReturn("abcde12345");
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(inv -> inv.getArgument(0));

        Trainer created = service.createTrainer(dto);

        assertThat(created.getUsername()).isEqualTo("Ana.Torres");
        assertThat(created.getPassword()).isEqualTo("abcde12345");
        assertThat(created.getIsActive()).isTrue();
        assertThat(created.getSpecialization()).isSameAs(yoga);
    }

    @Test
    void createTrainer_unknownType_throwsAndDoesNotSave() {
        when(credentialsGenerator.generateUsername(any(), any(), any())).thenReturn("Ana.Torres");
        when(trainingTypeRepository.findByNameIgnoreCase("Nope")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createTrainer(dto("Nope")))
                .isInstanceOf(NoSuchElementException.class);
        verify(trainerRepository, never()).save(any());
    }

    @Test
    void updateTrainer_existing_setsTypeAndSaves() {
        Trainer existing = trainer("Ana.Torres", "pw", true);
        TrainerDTO dto = dto("Pilates");
        TrainingType pilates = new TrainingType("Pilates");
        when(trainerRepository.findByUsername("Ana.Torres")).thenReturn(Optional.of(existing));
        when(trainingTypeRepository.findByNameIgnoreCase("Pilates")).thenReturn(Optional.of(pilates));
        when(trainerRepository.save(existing)).thenReturn(existing);

        Trainer updated = service.updateTrainer("Ana.Torres", dto);

        assertThat(updated.getSpecialization()).isSameAs(pilates);
        verify(trainerMapper).updateTrainerFromDTO(dto, existing);
    }

    @Test
    void updateTrainer_unknownUsername_throws() {
        when(trainerRepository.findByUsername("No.Existe")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateTrainer("No.Existe", dto("Yoga")))
                .isInstanceOf(NoSuchElementException.class);
        verify(trainerRepository, never()).save(any());
    }

    @Test
    void updateTrainer_unknownType_throws() {
        when(trainerRepository.findByUsername("Ana.Torres")).thenReturn(Optional.of(trainer("Ana.Torres", "pw", true)));
        when(trainingTypeRepository.findByNameIgnoreCase("Nope")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateTrainer("Ana.Torres", dto("Nope")))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void getTrainerByUsername_delegatesToRepository() {
        Trainer t = trainer("Ana.Torres", "pw", true);
        when(trainerRepository.findByUsername("Ana.Torres")).thenReturn(Optional.of(t));

        assertThat(service.getTrainerByUsername("Ana.Torres")).containsSame(t);
    }

    @Test
    void changePassword_correctOld_changes() {
        Trainer t = trainer("Ana.Torres", "old", true);
        when(trainerRepository.findByUsername("Ana.Torres")).thenReturn(Optional.of(t));

        assertThat(service.changePassword("Ana.Torres", "old", "new")).isTrue();
        assertThat(t.getPassword()).isEqualTo("new");
    }

    @Test
    void changePassword_wrongOld_returnsFalse() {
        Trainer t = trainer("Ana.Torres", "old", true);
        when(trainerRepository.findByUsername("Ana.Torres")).thenReturn(Optional.of(t));

        assertThat(service.changePassword("Ana.Torres", "bad", "new")).isFalse();
        assertThat(t.getPassword()).isEqualTo("old");
    }

    @Test
    void toggleStatus_flipsAndReturnsNewState() {
        Trainer t = trainer("Ana.Torres", "pw", false);
        when(trainerRepository.findByUsername("Ana.Torres")).thenReturn(Optional.of(t));

        assertThat(service.toggleStatus("Ana.Torres")).isTrue();
    }

    @Test
    void toggleStatus_unknownUser_throws() {
        when(trainerRepository.findByUsername("No.Existe")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.toggleStatus("No.Existe")).isInstanceOf(NoSuchElementException.class);
    }
}
