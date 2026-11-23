package com.epam.gym_crm.entities;

public class TrainingType {
    private String name;

    public TrainingType() {
    }

    public TrainingType(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}