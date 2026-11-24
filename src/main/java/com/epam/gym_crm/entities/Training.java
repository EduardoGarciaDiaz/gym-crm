package com.epam.gym_crm.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "Trainings")
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    //@ManyToOne(fetch = FetchType.LAZY)
    @ManyToOne
    @JoinColumn(
            name = "trainee_id"
    )
    private Trainee trainee;

    @ManyToOne
    @JoinColumn(
            name = "trainer_id"
    )
    private Trainer trainerId;

    @Column(name = "TrainingName", length = 100, nullable = false)
    private String trainingName;

    @ManyToOne
    @JoinColumn(
            name = "training_type_id"
    )
    private TrainingType trainingType;

    @Column(name = "TrainingDate", nullable = false)
    private LocalDate trainingDate;

    @Column(name = "TrainingDuration", nullable = false)
    private int trainingDuration;

    public Training() {
    }

    public Training(Trainee traineeId, Trainer trainerId, String trainingName,
                    TrainingType trainingType, LocalDate trainingDate, int trainingDuration) {
        this.trainee = traineeId;
        this.trainerId = trainerId;
        this.trainingName = trainingName;
        this.trainingType = trainingType;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
    }

    public Training(Long id, Trainee traineeId, Trainer trainerId, String trainingName,
                    TrainingType trainingType, LocalDate trainingDate, int trainingDuration) {
        this.id = id;
        this.trainee = traineeId;
        this.trainerId = trainerId;
        this.trainingName = trainingName;
        this.trainingType = trainingType;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Trainee getTrainee() {
        return trainee;
    }

    public void setTrainee(Trainee trainee) {
        this.trainee = trainee;
    }

    public Trainer getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(Trainer trainerId) {
        this.trainerId = trainerId;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public TrainingType getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(TrainingType trainingType) {
        this.trainingType = trainingType;
    }

    public LocalDate getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(LocalDate trainingDate) {
        this.trainingDate = trainingDate;
    }

    public int getTrainingDuration() {
        return trainingDuration;
    }

    public void setTrainingDuration(int trainingDuration) {
        this.trainingDuration = trainingDuration;
    }
}