package com.epam.gym_crm.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Trainees")
public class Trainee extends User {

//    @Id
//    @GeneratedValue
//    @Column(name = "ID")
//    private Long id;
    @Column(name = "DateOfBirth")
    private LocalDate dateOfBirth;

    @Column(name = "Address", length = 150)
    private String address;

    @OneToMany(
            mappedBy = "trainee",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    private List<Training> trainings = new ArrayList<>();

    public Trainee() {
    }

    public Trainee(String firstName, String lastName, String username, String password, boolean isActive, LocalDate birthdate, String address) {
        super(firstName, lastName, username, password, isActive);
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
