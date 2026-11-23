package com.epam.gym_crm.entities;

public class Trainer extends User {
    private TrainingType specialization;
    private Long userId;

    public Trainer() {
    }

    public Trainer(String firstName, String lastName, String username, String password,
                   boolean active, TrainingType specialization, Long userId) {
        super(firstName, lastName, username, password, active);
        this.specialization = specialization;
        this.userId = userId;
    }

    public TrainingType getSpecialization() { return specialization; }
    public void setSpecialization(TrainingType specialization) { this.specialization = specialization; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}