package com.epam.gym_crm.repositories;

import com.epam.gym_crm.entities.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class TraineeRepository {
    private Map<Long, Trainee> trainees;

    @Autowired
    public void setTrainees(@Qualifier("traineeStorage") Map<Long, Trainee> trainees) {
        this.trainees = trainees;
    }

    public synchronized Trainee saveTrainee(Trainee trainee) {
        if (trainee.getUserId() == null) {
            trainee.setUserId(nextId());
        }

        trainees.put(trainee.getUserId(), trainee);
        return trainee;
    }

    public boolean deleteTraineeById(Long id) {
        if (id != null && id > 0) {
            return trainees.remove(id) != null;
        } else {
            return false;
        }
    }

    public Optional<Trainee> findTraineeById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(trainees.get(id));
    }

    public boolean existsByUsername(String username) {
        return trainees.values().stream()
                .anyMatch(t -> username.equals(t.getUsername()));
    }

    private long nextId() {
        return trainees.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L) + 1;
    }

}
