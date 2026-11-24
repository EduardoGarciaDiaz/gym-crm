package com.epam.gym_crm.services;

import com.epam.gym_crm.dtos.TrainerDTO;
import com.epam.gym_crm.entities.Trainer;
import com.epam.gym_crm.entities.TrainingType;
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

    private TrainerRepository trainerRepository;
    private CredentialsGenerator credentialsGenerator;

    @Autowired
    public void setTrainerRepository(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Autowired
    public void setCredentialsGenerator(CredentialsGenerator credentialsGenerator) {
        this.credentialsGenerator = credentialsGenerator;
    }

    public Trainer createTrainer(TrainerDTO trainer) {
        String firstName = trainer.getFirstName();
        String lastName = trainer.getLastName();
        boolean isActive = true;
        log.info("Creating trainer {} {} with specialization {}", firstName, lastName, trainer.getSpecialization());
        TrainingType trainingType = new TrainingType(trainer.getSpecialization());

        Trainer newTrainer = new Trainer();
        newTrainer.setFirstName(firstName);
        newTrainer.setLastName(lastName);
        newTrainer.setUsername(credentialsGenerator.generateUsername(firstName, lastName, trainerRepository::existsByUsername));
        newTrainer.setPassword(credentialsGenerator.generatePassword());
        newTrainer.setIsActive(isActive);
        newTrainer.setSpecialization(trainingType);
        log.debug("Credentials generated for trainer, username={}", newTrainer.getUsername());

        newTrainer = trainerRepository.saveTrainer(newTrainer);
        log.info("Trainer created with id={} and username={}", newTrainer.getUserId(), newTrainer.getUsername());
        return newTrainer;
    }

    public Trainer updateTrainer(Long id, TrainerDTO trainerDTO) {
        log.info("Updating trainer with id={}", id);
        Trainer trainer = trainerRepository.findTrainerById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot update: trainer with id={} not found", id);
                    return new NoSuchElementException("Trainer not found: " + id);
                });

        trainer.setFirstName(trainerDTO.getFirstName());
        trainer.setLastName(trainerDTO.getLastName());
        trainer.setSpecialization(new TrainingType(trainerDTO.getSpecialization()));

        Trainer updated = trainerRepository.saveTrainer(trainer);
        log.info("Trainer with id={} updated", id);
        return updated;
    }

    public Optional<Trainer> getTrainerById(Long id) {
        log.debug("Looking up trainer with id={}", id);
        Optional<Trainer> trainer = trainerRepository.findTrainerById(id);

        if (trainer.isEmpty()) {
            log.debug("Trainer with id={} not found", id);
        }
        return trainer;
    }
}