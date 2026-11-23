package com.epam.gym_crm.entities;

import java.time.LocalDate;

public class Trainee extends User {

    private LocalDate dateOfBirth;
    private String address;
    private Long userId;

    public Trainee() {
    }

    public Trainee(String firstName, String lastName, String username, String password, Boolean isActive, LocalDate birthdate, String address, Long userId) {
        super(firstName, lastName, username, password, isActive);
        this.dateOfBirth = birthdate;
        this.address = address;
        this.userId = userId;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
