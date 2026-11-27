package com.epam.gym_crm.dtos;

import java.time.LocalDate;

public record TrainerTrainingCriteria(LocalDate from, LocalDate to, String traineeName) {

    public TrainerTrainingCriteria {
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("'from' date must not be after 'to' date");
        }
    }

    public static TrainerTrainingCriteria none() {
        return new TrainerTrainingCriteria(null, null, null);
    }
}
