package com.epam.gym_crm.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class TrainerDTO extends UserDTO {
    @NotBlank
    private String specialization;

    public TrainerDTO() {
    }

    public TrainerDTO(String firstName, String lastName, String specialization) {
        super(firstName, lastName);
        this.specialization = specialization;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}
