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

    @Autowired
    private TraineeRepository traineeRepository;
    private CredentialsGenerator credentialsGenerator;

    public TraineeService(CredentialsGenerator credentialsGenerator) {
        this.credentialsGenerator = credentialsGenerator;
    }

    public int createTrainee(TraineeDTO trainee) {
        String firstName = trainee.getFirstName();
        String lastName = trainee.getLastName();

        Trainee newTrainee = new Trainee();
        newTrainee.setFirstName(firstName);
        newTrainee.setLastName(lastName);
        newTrainee.setUsername(credentialsGenerator.generateUsername(firstName, lastName));
        newTrainee.setPassword(credentialsGenerator.generatePassword());
        newTrainee.setActive(true); //Should it be always true or should it come from the TraineeDTO?
        newTrainee.setBirthdate(trainee.getBirthdate());
        newTrainee.setAddress(trainee.getAddress());
        int id = traineeRepository.createTrainee(trainee);
        newTrainee.setUserId(id);

        return id;
    }

    public boolean deleteTrainee(int traineeId) {
        boolean isDeleted = traineeRepository.deleteTrainee(traineeId);
        return isDeleted;
    }


}
