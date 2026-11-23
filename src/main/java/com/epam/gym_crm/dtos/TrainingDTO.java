package com.epam.gym_crm.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class TrainingDTO {
    @NotNull
    private Long traineeId;
    @NotNull
    private Long trainerId;
    @NotBlank
    private String trainingName;
    @NotBlank
    private String trainingType;
    @NotNull
    private LocalDate trainingDate;
    @Positive
    private int trainingDuration;

    public TrainingDTO() {
    }

    public TrainingDTO(Long traineeId, Long trainerId, String trainingName,
                       String trainingType, LocalDate trainingDate, int trainingDuration) {
        this.traineeId = traineeId;
        this.trainerId = trainerId;
        this.trainingName = trainingName;
        this.trainingType = trainingType;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
    }

    public Long getTraineeId() { return traineeId; }
    public void setTraineeId(Long traineeId) { this.traineeId = traineeId; }

    public Long getTrainerId() { return trainerId; }
    public void setTrainerId(Long trainerId) { this.trainerId = trainerId; }

    public String getTrainingName() { return trainingName; }
    public void setTrainingName(String trainingName) { this.trainingName = trainingName; }

    public String getTrainingType() { return trainingType; }
    public void setTrainingType(String trainingType) { this.trainingType = trainingType; }

    public LocalDate getTrainingDate() { return trainingDate; }
    public void setTrainingDate(LocalDate trainingDate) { this.trainingDate = trainingDate; }

    public int getTrainingDuration() { return trainingDuration; }
    public void setTrainingDuration(int trainingDuration) { this.trainingDuration = trainingDuration; }
}