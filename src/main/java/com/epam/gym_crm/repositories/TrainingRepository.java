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
    private Map<Long, Training> trainings;

    @Autowired
    public void setTrainings(@Qualifier("trainingStorage") Map<Long, Training> trainings) {
        this.trainings = trainings;
    }

    public synchronized Training saveTraining(Training training) {
        if (training.getId() == null) {
            training.setId(nextId());
        }

        trainings.put(training.getId(), training);
        return training;
    }

    public Optional<Training> findTrainingById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(trainings.get(id));
    }

    private long nextId() {
        return trainings.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L) + 1;
    }
}
