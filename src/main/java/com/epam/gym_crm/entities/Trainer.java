package com.epam.gym_crm.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Trainers")
public class Trainer extends User {
    @ManyToOne
    @JoinColumn(
            name = "training_type_id"
    )
    //@JsonBackReference
    private TrainingType specialization;

    public Trainer() {
    }

    public Trainer(String firstName, String lastName, String username, String password,
                   boolean active, TrainingType specialization) {
        super(firstName, lastName, username, password, active);
        this.specialization = specialization;
    }

    public TrainingType getSpecialization() {
        return specialization;
    }

    public void setSpecialization(TrainingType specialization) {
        this.specialization = specialization;
    }
}