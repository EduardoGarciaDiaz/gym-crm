package com.epam.gym_crm.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "TrainingTypes")
public class TrainingType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TrainingTypeName", length = 100)
    private String name;

    public TrainingType() {
    }

    public TrainingType(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}