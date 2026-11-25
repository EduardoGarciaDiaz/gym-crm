package com.epam.gym_crm.services;

import com.epam.gym_crm.dtos.TraineeDTO;
import com.epam.gym_crm.entities.Trainee;
import com.epam.gym_crm.mappers.TraineeMapper;
import com.epam.gym_crm.repositories.ITraineeRepository;
import com.epam.gym_crm.repositories.IUserRepository;
import com.epam.gym_crm.utils.CredentialsGenerator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Validated
@Transactional
public class TraineeService {

    private static final Logger log = LoggerFactory.getLogger(TraineeService.class);

    private ITraineeRepository traineeRepository;
    private IUserRepository userRepository;
    private CredentialsGenerator credentialsGenerator;
    private TraineeMapper traineeMapper;

    @Autowired
    public void setTraineeRepository(ITraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    @Autowired
    public void setUserRepository(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setCredentialsGenerator(CredentialsGenerator credentialsGenerator) {
        this.credentialsGenerator = credentialsGenerator;
    }

    @Autowired
    public void setTraineeMapper(TraineeMapper traineeMapper) {
        this.traineeMapper = traineeMapper;
    }

    public Trainee createTrainee(@NotNull @Valid TraineeDTO traineeDTO) {
        String firstName = traineeDTO.getFirstName();
        String lastName = traineeDTO.getLastName();
        String username = credentialsGenerator.generateUsername(
                firstName,
                lastName,
                userRepository::existsByUsername
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
        long deleted = traineeRepository.deleteByUsername(username);

        if (deleted > 0) {
            log.info("Trainee with username={} deleted along with their trainings", username);
            return true;
        }
        log.warn("Trainee with username={} not found, nothing deleted", username);
        return false;
    }

    public Trainee updateTrainee(Long id,@NotNull @Valid TraineeDTO traineeDTO) {
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

    @Transactional(readOnly = true)
    public Optional<Trainee> getTraineeByUsername(String username) {
        log.debug("Looking up trainee with username={}", username);
        return traineeRepository.findByUsername(username);
    }

    public boolean changePassword(@NotBlank String username,
                                  @NotBlank String oldPassword,
                                  @NotBlank String newPassword) {
        Trainee trainee = findOrThrow(username);

        if (!trainee.getPassword().equals(oldPassword)) {
            log.warn("Password change rejected for trainee {}: wrong current password", username);
            return false;
        }

        trainee.setPassword(newPassword);
        log.info("Password changed for trainee {}", username);
        return true;
    }

    public boolean toggleStatus(@NotBlank String username) {
        Trainee trainee = findOrThrow(username);

        trainee.setIsActive(!trainee.getIsActive());
        log.info("Trainee {} is now {}", username, trainee.getIsActive() ? "active" : "inactive");
        return trainee.getIsActive();
    }

    private Trainee findOrThrow(String username) {
        return traineeRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainee with username={} not found", username);
                    return new NoSuchElementException("Trainee not found: " + username);
                });
    }
}