package com.epam.gym_crm.dtos;

import java.time.LocalDate;

public record TraineeTrainingCriteria(LocalDate from, LocalDate to, String trainerName, String trainingType) {

    public TraineeTrainingCriteria {
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("'from' date must not be after 'to' date");
        }
    }

    public static TraineeTrainingCriteria none() {
        return new TraineeTrainingCriteria(null, null, null, null);
    }
}
