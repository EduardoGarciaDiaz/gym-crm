package com.epam.gym_crm.services;

//Trainee Service class should support possibility to create/update/delete/select Trainee profile.

import com.epam.gym_crm.dtos.TraineeDTO;
import com.epam.gym_crm.entities.Trainee;
import com.epam.gym_crm.repositories.TraineeRepository;
import com.epam.gym_crm.utils.CredentialsGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TraineeService {

    private TraineeRepository traineeRepository;
    private CredentialsGenerator credentialsGenerator;

    @Autowired
    public void setTraineeRepository(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    @Autowired
    public void setCredentialsGenerator(CredentialsGenerator credentialsGenerator) {
        this.credentialsGenerator = credentialsGenerator;
    }

    public Trainee createTrainee(TraineeDTO trainee) {
        String firstName = trainee.getFirstName();
        String lastName = trainee.getLastName();
        boolean isActive = true;

        Trainee newTrainee = new Trainee();
        newTrainee.setFirstName(firstName);
        newTrainee.setLastName(lastName);
        newTrainee.setUsername(credentialsGenerator.generateUsername(firstName, lastName, traineeRepository::existsByUsername));
        newTrainee.setPassword(credentialsGenerator.generatePassword());
        newTrainee.setActive(isActive);
        newTrainee.setDateOfBirth(trainee.getDateOfBirth());
        newTrainee.setAddress(trainee.getAddress());

        newTrainee = traineeRepository.saveTrainee(newTrainee);
        return newTrainee;
    }

    public boolean deleteTrainee(Long traineeId) {
        boolean isDeleted = traineeRepository.deleteTraineeById(traineeId);
        return isDeleted;
    }


}
