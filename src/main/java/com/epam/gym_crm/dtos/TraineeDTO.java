package com.epam.gym_crm.dtos;

import jakarta.validation.constraints.NotEmpty;

import java.util.Date;

public class TraineeDTO extends UserDTO {
    @NotEmpty
    private Date birthdate;
    @NotEmpty
    private String address;

    public TraineeDTO() {
    }

    public TraineeDTO(String firstName, String lastName, Boolean isActive, Date birthdate, String address) {
        super(firstName, lastName, isActive);
        this.birthdate = birthdate;
        this.address = address;
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
}
