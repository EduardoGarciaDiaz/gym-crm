package com.epam.gym_crm.dtos;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class TraineeDTO extends UserDTO {
    @Past
    private LocalDate dateOfBirth;
    @Size(max = 150)
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
