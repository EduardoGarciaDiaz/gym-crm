package com.epam.gym_crm.services;

import com.epam.gym_crm.dtos.TrainerDTO;
import com.epam.gym_crm.entities.Trainer;
import com.epam.gym_crm.entities.TrainingType;
import com.epam.gym_crm.mappers.TrainerMapper;
import com.epam.gym_crm.repositories.ITrainerRepository;
import com.epam.gym_crm.repositories.TrainerRepository;
import com.epam.gym_crm.utils.CredentialsGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class TrainerService {

    private static final Logger log = LoggerFactory.getLogger(TrainerService.class);

    private ITrainerRepository trainerRepository;
    private CredentialsGenerator credentialsGenerator;
    private TrainerMapper trainerMapper;

    @Autowired
    public void setTrainerRepository(ITrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Autowired
    public void setCredentialsGenerator(CredentialsGenerator credentialsGenerator) {
        this.credentialsGenerator = credentialsGenerator;
    }

    @Autowired
    public void setTrainerMapper(TrainerMapper trainerMapper) {
        this.trainerMapper = trainerMapper;
    }

    public Trainer createTrainer(TrainerDTO trainerDTO) {
        String firstName = trainerDTO.getFirstName();
        String lastName = trainerDTO.getLastName();
        String username = credentialsGenerator.generateUsername(firstName, lastName, trainerRepository::existsByUsername);
        Trainer newTrainer = trainerMapper.dtoToTrainer(trainerDTO);

        newTrainer.setUsername(username);
        newTrainer.setPassword(credentialsGenerator.generatePassword());
        newTrainer.setIsActive(true);

        newTrainer = trainerRepository.save(newTrainer);
        log.info("Trainer created with id={} and username={}", newTrainer.getId(), newTrainer.getUsername());
        return newTrainer;
    }

    public Trainer updateTrainer(Long id, TrainerDTO trainerDTO) {
        log.info("Updating trainer with id={}", id);
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot update: trainer with id={} not found", id);
                    return new NoSuchElementException("Trainer not found: " + id);
                });

        trainerMapper.updateTrainerFromDTO(trainerDTO, trainer);

        Trainer updated = trainerRepository.save(trainer);
        log.info("Trainer with id={} updated", id);
        return updated;
    }

    public Trainer getTrainerByUsername(String username) {
        log.debug("Looking up trainer with username={}", username);
        Trainer trainer = trainerRepository.findByUsernameIgnoreCase(username);

        if (trainer == null) {
            log.debug("Trainer with username={} not found", username);
        }

        return trainer;
    }

    //TODO:
    public boolean changePassword(String oldPassword, String newPassword) {
        return true;
    }

    //TODO:
    public boolean changeTraineeStatus() {
        return true;
    }
}