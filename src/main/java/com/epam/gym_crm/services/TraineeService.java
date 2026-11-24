package com.epam.gym_crm.services;

import com.epam.gym_crm.dtos.TraineeDTO;
import com.epam.gym_crm.entities.Trainee;
import com.epam.gym_crm.mappers.TraineeMapper;
import com.epam.gym_crm.repositories.ITraineeRepository;
import com.epam.gym_crm.utils.CredentialsGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class TraineeService {

    private static final Logger log = LoggerFactory.getLogger(TraineeService.class);

    private ITraineeRepository traineeRepository;
    private CredentialsGenerator credentialsGenerator;
    private TraineeMapper traineeMapper;

    @Autowired
    public void setTraineeRepository(ITraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    @Autowired
    public void setCredentialsGenerator(CredentialsGenerator credentialsGenerator) {
        this.credentialsGenerator = credentialsGenerator;
    }

    @Autowired
    public void setTraineeMapper(TraineeMapper traineeMapper) {
        this.traineeMapper = traineeMapper;
    }

    public Trainee createTrainee(TraineeDTO traineeDTO) {
        String firstName = traineeDTO.getFirstName();
        String lastName = traineeDTO.getLastName();
        String username = credentialsGenerator.generateUsername(
                firstName,
                lastName,
                traineeRepository::existsByUsername
        );

        Trainee newTrainee = traineeMapper.dtoToTrainee(traineeDTO);
        newTrainee.setUsername(username);
        newTrainee.setPassword(credentialsGenerator.generatePassword());
        newTrainee.setIsActive(true);

        newTrainee = traineeRepository.save(newTrainee);
        log.info("Trainee created with id={} and username={}", newTrainee.getId(), newTrainee.getUsername());
        return newTrainee;
        //TODO: Return a response DTO
    }


    public boolean deleteTraineeByUsername(String username) {
        log.info("Deleting trainee with username={}", username);
        if (username != null && traineeRepository.existsByUsername(username)) {
            traineeRepository.deleteByUsername(username);
            log.info("Trainee with id={} deleted", username);
            return true;
        } else {
            log.warn("Trainee with id={} not found, nothing deleted", username);
            return false;
        }
    }

    public Trainee updateTrainee(Long id, TraineeDTO traineeDTO) {
        log.info("Updating trainee with id={}", id);

        Trainee trainee = traineeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot update: trainee with id={} not found", id);
                    return new NoSuchElementException("Trainee not found: " + id);
                });

        traineeMapper.updateTraineeFromDTO(traineeDTO, trainee);

        Trainee updated = traineeRepository.save(trainee);
        log.info("Trainee with id={} updated", id);
        return updated;
    }

    public Trainee getTraineeByUsername(String username) {
        log.debug("Looking up trainee with username={}", username);
        Trainee trainee = traineeRepository.findByUsernameIgnoreCase(username);

        if (trainee == null) {
            log.debug("Trainee with username={} not found", username);
        }
        return trainee;
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