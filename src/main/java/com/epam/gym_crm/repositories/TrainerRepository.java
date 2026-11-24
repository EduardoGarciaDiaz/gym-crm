package com.epam.gym_crm.repositories;

import com.epam.gym_crm.entities.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class TrainerRepository {
    private Map<Long, Trainer> trainers;

    @Autowired
    public void setTrainers(@Qualifier("trainerStorage") Map<Long, Trainer> trainers) {
        this.trainers = trainers;
    }

    public synchronized Trainer saveTrainer(Trainer trainer) {
        if (trainer.getId() == null) {
            trainer.setId(nextId());
        }

        trainers.put(trainer.getId(), trainer);
        return trainer;
    }

    public Optional<Trainer> findTrainerById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(trainers.get(id));
    }

    public boolean existsByUsername(String username) {
        return trainers.values().stream()
                .anyMatch( t -> username.equals(t.getUsername()));
    }

    private long nextId() {
        return trainers.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L) + 1;
    }

}
