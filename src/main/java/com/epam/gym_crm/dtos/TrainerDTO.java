package com.epam.gym_crm.dtos;

import jakarta.validation.constraints.NotEmpty;

public class TrainerDTO extends UserDTO {
    @NotEmpty
    private String specialization;
    @NotEmpty
    private String userId;

    public TrainerDTO() {
    }

    public TrainerDTO(String firstName, String lastName, Boolean isActive, String specialization) {
        super(firstName, lastName, isActive);
        this.specialization = specialization;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}
