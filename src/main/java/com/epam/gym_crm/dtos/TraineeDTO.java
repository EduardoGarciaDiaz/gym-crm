package com.epam.gym_crm.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public class TraineeDTO extends UserDTO {
    @NotNull
    @Past
    private LocalDate dateOfBirth;
    @NotBlank
    private String address;

    public TraineeDTO() {
    }

    public TraineeDTO(String firstName, String lastName, boolean isActive, LocalDate birthdate, String address) {
        super(firstName, lastName);
        this.dateOfBirth = birthdate;
        this.address = address;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
