package com.epam.gym_crm.repositories;

import com.epam.gym_crm.dtos.TraineeDTO;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class TraineeRepository {
    private Map<Integer, TraineeDTO> trainees;

    public int createTrainee(TraineeDTO trainee) {
        int nextId = trainees.keySet()
                .stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0) + 1;
        trainees.put(nextId, trainee);
        return nextId;
    }

    public boolean deleteTrainee(int traineeId) {
        if (trainees.containsKey(traineeId)) {
            trainees.remove(traineeId);
        }

        return true;
    }

}
