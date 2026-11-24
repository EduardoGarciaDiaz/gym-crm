package com.epam.gym_crm.repositories;

import com.epam.gym_crm.dtos.TrainingDTO;
import com.epam.gym_crm.entities.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class TrainingRepository {
    private ITrainingRepository trainingRepository;

    @Autowired
    public void setTrainingRepository(ITrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }
    //private Map<Long, Training> trainings;
/*
    @Autowired
    public void setTrainings(@Qualifier("trainingStorage") Map<Long, Training> trainings) {
        this.trainings = trainings;
    }
 */

    //TODO: Verify if it should receive a Training or a TrainingDTO
    public synchronized Training saveTraining(Training training) {
        Training newTraining = trainingRepository.save(training);

        return newTraining;
    }

    //TODO: Find by name
    public Optional<Training> findTrainingById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        return trainingRepository.findById(id);
    }
}
