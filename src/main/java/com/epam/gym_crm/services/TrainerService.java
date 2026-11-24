package com.epam.gym_crm.services;

import com.epam.gym_crm.dtos.TrainerDTO;
import com.epam.gym_crm.entities.Trainer;
import com.epam.gym_crm.entities.TrainingType;
import com.epam.gym_crm.repositories.TrainerRepository;
import com.epam.gym_crm.utils.CredentialsGenerator;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class TrainerService {
    private TrainerRepository trainerRepository;
    private CredentialsGenerator credentialsGenerator;

    public void setTrainerService(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    public void setCredentialsGenerator(CredentialsGenerator credentialsGenerator) {
        this.credentialsGenerator = credentialsGenerator;
    }

    public Trainer createTrainer(TrainerDTO trainer) {
        String firstName = trainer.getFirstName();
        String lastName = trainer.getLastName();
        boolean isActive = true;
        TrainingType trainingType = new TrainingType(trainer.getSpecialization());

        Trainer newTrainer = new Trainer();
        newTrainer.setFirstName(firstName);
        newTrainer.setLastName(lastName);
        newTrainer.setUsername(credentialsGenerator.generateUsername(firstName, lastName, trainerRepository::existByUsername));
        newTrainer.setPassword(credentialsGenerator.generatePassword());
        newTrainer.setIsActive(isActive);
        newTrainer.setSpecialization(trainingType);

        newTrainer = trainerRepository.saveTrainer(newTrainer);
        return newTrainer;
    }

    public Trainer updateTrainer(Long id, TrainerDTO trainerDTO) {
        Trainer trainer = trainerRepository.findTrainerById(id)
                .orElseThrow(() -> {
                    return new NoSuchElementException("Trainer not found: " + id);
                });

        trainer.setFirstName(trainerDTO.getFirstName());
        trainer.setLastName(trainerDTO.getLastName());
        trainer.setSpecialization(new TrainingType(trainerDTO.getSpecialization()));

        return trainerRepository.saveTrainer(trainer);
    }

    public Optional<Trainer> getTrainerById(Long id) {
        return trainerRepository.findTrainerById(id);
    }
}
