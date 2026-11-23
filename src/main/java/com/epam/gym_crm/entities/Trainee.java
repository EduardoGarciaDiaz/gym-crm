package com.epam.gym_crm.entities;

import jakarta.validation.constraints.NotEmpty;

import java.util.Date;

public class Trainee extends User {

    @NotEmpty
    private Date birthdate;
    @NotEmpty
    private String address;
    @NotEmpty
    private int userId;

    public Trainee() {
    }

    public Trainee(String firstName, String lastName, String username, String password, Boolean isActive, Date birthdate, String address, int userId) {
        super(firstName, lastName, username, password, isActive);
        this.birthdate = birthdate;
        this.address = address;
        this.userId = userId;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
