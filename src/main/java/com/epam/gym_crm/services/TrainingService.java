package com.epam.gym_crm.services;

import com.epam.gym_crm.dtos.TrainingDTO;
import com.epam.gym_crm.entities.Trainee;
import com.epam.gym_crm.entities.Trainer;
import com.epam.gym_crm.entities.Training;
import com.epam.gym_crm.entities.TrainingType;
import com.epam.gym_crm.repositories.ITraineeRepository;
import com.epam.gym_crm.repositories.ITrainerRepository;
import com.epam.gym_crm.repositories.ITrainingRepository;
import com.epam.gym_crm.repositories.ITrainingTypeRepository;
import com.epam.gym_crm.repositories.TrainingSpecifications;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Validated
@Transactional
public class TrainingService {

    private static final Logger log = LoggerFactory.getLogger(TrainingService.class);

    private ITrainingRepository trainingRepository;
    private ITraineeRepository traineeRepository;
    private ITrainerRepository trainerRepository;
    private ITrainingTypeRepository trainingTypeRepository;

    @Autowired
    public void setTrainingRepository(ITrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    @Autowired
    public void setTraineeRepository(ITraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    @Autowired
    public void setTrainerRepository(ITrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Autowired
    public void setTrainingTypeRepository(ITrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    public Training createTraining(@NotNull @Valid TrainingDTO trainingDTO) {
        log.info("Creating training '{}' for traineeId={} with trainerId={}",
                trainingDTO.getTrainingName(), trainingDTO.getTraineeId(), trainingDTO.getTrainerId());

        Trainee trainee = traineeRepository.findById(trainingDTO.getTraineeId())
                .orElseThrow(() -> new NoSuchElementException("Trainee not found: " + trainingDTO.getTraineeId()));
        Trainer trainer = trainerRepository.findById(trainingDTO.getTrainerId())
                .orElseThrow(() -> new NoSuchElementException("Trainer not found: " + trainingDTO.getTrainerId()));
        TrainingType type = trainingTypeRepository.findByNameIgnoreCase(trainingDTO.getTrainingType())
                .orElseThrow(() -> new NoSuchElementException("Training type not found: " + trainingDTO.getTrainingType()));

        Training newTraining = new Training();
        newTraining.setTrainee(trainee);
        newTraining.setTrainer(trainer);
        newTraining.setTrainingName(trainingDTO.getTrainingName());
        newTraining.setTrainingType(type);
        newTraining.setTrainingDate(trainingDTO.getTrainingDate());
        newTraining.setTrainingDuration(trainingDTO.getTrainingDuration());

        newTraining = trainingRepository.save(newTraining);
        log.info("Training created with id={}", newTraining.getId());
        return newTraining;
    }

    private static final Sort BY_DATE = Sort.by("trainingDate", "id");

    @Transactional(readOnly = true)
    public List<Training> getTraineeTrainings(@NotBlank String traineeUsername, LocalDate from, LocalDate to,
                                              String trainerName, String trainingType) {
        if (!traineeRepository.existsByUsername(traineeUsername)) {
            throw new NoSuchElementException("Trainee not found: " + traineeUsername);
        }
        log.debug("Searching trainings of trainee {} from={} to={} trainer={} type={}",
                traineeUsername, from, to, trainerName, trainingType);
        return trainingRepository.findAll(
                TrainingSpecifications.forTrainee(traineeUsername, from, to, trainerName, trainingType), BY_DATE);
    }

    /** Req. 15: trainings de un trainer; todos los criterios (menos el username) son opcionales. */
    @Transactional(readOnly = true)
    public List<Training> getTrainerTrainings(@NotBlank String trainerUsername, LocalDate from, LocalDate to,
                                              String traineeName) {
        if (!trainerRepository.existsByUsername(trainerUsername)) {
            throw new NoSuchElementException("Trainer not found: " + trainerUsername);
        }
        log.debug("Searching trainings of trainer {} from={} to={} trainee={}",
                trainerUsername, from, to, traineeName);
        return trainingRepository.findAll(
                TrainingSpecifications.forTrainer(trainerUsername, from, to, traineeName), BY_DATE);
    }
}
