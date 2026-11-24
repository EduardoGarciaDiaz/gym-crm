package com.epam.gym_crm.services;

import com.epam.gym_crm.dtos.TraineeDTO;
import com.epam.gym_crm.entities.Trainee;
import com.epam.gym_crm.repositories.TraineeRepository;
import com.epam.gym_crm.utils.CredentialsGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class TraineeService {

    private static final Logger log = LoggerFactory.getLogger(TraineeService.class);

    private TraineeRepository traineeRepository;
    private CredentialsGenerator credentialsGenerator;

    @Autowired
    public void setTraineeRepository(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    @Autowired
    public void setCredentialsGenerator(CredentialsGenerator credentialsGenerator) {
        this.credentialsGenerator = credentialsGenerator;
    }

    public Trainee createTrainee(TraineeDTO trainee) {
        String firstName = trainee.getFirstName();
        String lastName = trainee.getLastName();
        boolean isActive = true;
        log.info("Creating trainee {} {}", firstName, lastName);

        Trainee newTrainee = new Trainee();
        newTrainee.setFirstName(firstName);
        newTrainee.setLastName(lastName);
        newTrainee.setUsername(credentialsGenerator.generateUsername(firstName, lastName, traineeRepository::existsByUsername));
        newTrainee.setPassword(credentialsGenerator.generatePassword());
        newTrainee.setIsActive(isActive);
        newTrainee.setDateOfBirth(trainee.getDateOfBirth());
        newTrainee.setAddress(trainee.getAddress());
        log.debug("Credentials generated for trainee, username={}", newTrainee.getUsername());

        newTrainee = traineeRepository.saveTrainee(newTrainee);
        log.info("Trainee created with id={} and username={}", newTrainee.getUserId(), newTrainee.getUsername());
        return newTrainee;
    }

    public boolean deleteTrainee(Long traineeId) {
        log.info("Deleting trainee with id={}", traineeId);
        boolean isDeleted = traineeRepository.deleteTraineeById(traineeId);

        if (isDeleted) {
            log.info("Trainee with id={} deleted", traineeId);
        } else {
            log.warn("Trainee with id={} not found, nothing deleted", traineeId);
        }
        return isDeleted;
    }

    public Trainee updateTrainee(Long id, TraineeDTO traineeDTO) {
        log.info("Updating trainee with id={}", id);
        Trainee trainee = traineeRepository.findTraineeById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot update: trainee with id={} not found", id);
                    return new NoSuchElementException("Trainee not found: " + id);
                });

        trainee.setFirstName(traineeDTO.getFirstName());
        trainee.setLastName(traineeDTO.getLastName());
        trainee.setDateOfBirth(traineeDTO.getDateOfBirth());
        trainee.setAddress(traineeDTO.getAddress());

        Trainee updated = traineeRepository.saveTrainee(trainee);
        log.info("Trainee with id={} updated", id);
        return updated;
    }

    public Optional<Trainee> getTraineeById(Long id) {
        log.debug("Looking up trainee with id={}", id);
        Optional<Trainee> trainee = traineeRepository.findTraineeById(id);

        if (trainee.isEmpty()) {
            log.debug("Trainee with id={} not found", id);
        }
        return trainee;
    }
}