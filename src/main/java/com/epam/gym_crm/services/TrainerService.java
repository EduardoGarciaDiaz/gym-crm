package com.epam.gym_crm.services;

import com.epam.gym_crm.dtos.TrainerDTO;
import com.epam.gym_crm.entities.Trainer;
import com.epam.gym_crm.entities.TrainingType;
import com.epam.gym_crm.mappers.TrainerMapper;
import com.epam.gym_crm.repositories.ITrainerRepository;
import com.epam.gym_crm.repositories.ITrainingTypeRepository;
import com.epam.gym_crm.repositories.IUserRepository;
import com.epam.gym_crm.repositories.TrainerRepository;
import com.epam.gym_crm.utils.CredentialsGenerator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class TrainerService {

    private static final Logger log = LoggerFactory.getLogger(TrainerService.class);

    private ITrainerRepository trainerRepository;
    private IUserRepository userRepository;
    private ITrainingTypeRepository trainingTypeRepository;
    private CredentialsGenerator credentialsGenerator;
    private TrainerMapper trainerMapper;

    @Autowired
    public void setTrainerRepository(ITrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Autowired
    public void setTraineeRepository(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setTrainingTypeRepository(ITrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Autowired
    public void setCredentialsGenerator(CredentialsGenerator credentialsGenerator) {
        this.credentialsGenerator = credentialsGenerator;
    }

    @Autowired
    public void setTrainerMapper(TrainerMapper trainerMapper) {
        this.trainerMapper = trainerMapper;
    }

    public Trainer createTrainer(@NotNull @Valid TrainerDTO trainerDTO) {
        String firstName = trainerDTO.getFirstName();
        String lastName = trainerDTO.getLastName();
        String username = credentialsGenerator.generateUsername(firstName, lastName, userRepository::existsByUsername);
        TrainingType type = trainingTypeRepository.findByNameIgnoreCase(trainerDTO.getSpecialization())
                .orElseThrow(() -> new NoSuchElementException("Training type not found: " + trainerDTO.getSpecialization()));
        Trainer newTrainer = trainerMapper.dtoToTrainer(trainerDTO);

        newTrainer.setSpecialization(type);
        newTrainer.setUsername(username);
        newTrainer.setPassword(credentialsGenerator.generatePassword());
        newTrainer.setIsActive(true);

        newTrainer = trainerRepository.save(newTrainer);
        log.info("Trainer created with id={} and username={}", newTrainer.getId(), newTrainer.getUsername());
        return newTrainer;
    }

    public Trainer updateTrainer(Long id,@NotNull @Valid TrainerDTO trainerDTO) {
        log.info("Updating trainer with id={}", id);
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot update: trainer with id={} not found", id);
                    return new NoSuchElementException("Trainer not found: " + id);
                });

        TrainingType type = trainingTypeRepository.findByNameIgnoreCase(trainerDTO.getSpecialization())
                .orElseThrow(() -> new NoSuchElementException("Training type not found: " + trainerDTO.getSpecialization()));
        trainer.setSpecialization(type);

        trainerMapper.updateTrainerFromDTO(trainerDTO, trainer);

        Trainer updated = trainerRepository.save(trainer);
        log.info("Trainer with id={} updated", id);
        return updated;
    }

    @Transactional(readOnly = true)
    public Optional<Trainer> getTrainerByUsername(String username) {
        log.debug("Looking up trainer with username={}", username);
        return trainerRepository.findByUsername(username);
    }

    public boolean changePassword(@NotBlank String username,
                                  @NotBlank String oldPassword,
                                  @NotBlank String newPassword) {
        Trainer trainer = findOrThrow(username);

        if (!trainer.getPassword().equals(oldPassword)) {
            log.warn("Password change rejected for trainer {}: wrong current password", username);
            return false;
        }

        trainer.setPassword(newPassword);
        log.info("Password changed for trainer {}", username);
        return true;
    }

    public boolean toggleStatus(@NotBlank String username) {
        Trainer trainer = findOrThrow(username);

        trainer.setIsActive(!trainer.getIsActive());
        log.info("Trainer {} is now {}", username, trainer.getIsActive() ? "active" : "inactive");
        return trainer.getIsActive();
    }

    private Trainer findOrThrow(String username) {
        return trainerRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainer with username={} not found", username);
                    return new NoSuchElementException("Trainer not found: " + username);
                });
    }
}