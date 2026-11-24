package com.epam.gym_crm.mappers;

import com.epam.gym_crm.dtos.TraineeDTO;
import com.epam.gym_crm.entities.Trainee;
import org.springframework.stereotype.Component;

@Component
public class TraineeMapper {
    public Trainee dtoToTrainee(TraineeDTO dto) {
        if (dto == null) {
            throw new NullPointerException("TraineeDTO can not be null");
        }

        Trainee trainee = new Trainee();

        trainee.setFirstName(dto.getFirstName());
        trainee.setLastName(dto.getLastName());
        trainee.setDateOfBirth(dto.getDateOfBirth());
        trainee.setAddress(dto.getAddress());

        return trainee;
    }

    public void updateTraineeFromDTO(TraineeDTO dto, Trainee trainee) {
        if (dto == null || trainee == null) {
            throw new IllegalArgumentException("Arguments TraineeDTO and Trainee cannot be null");
        }

        trainee.setFirstName(dto.getFirstName());
        trainee.setLastName(dto.getLastName());
        trainee.setDateOfBirth(dto.getDateOfBirth());
        trainee.setAddress(dto.getAddress());
    }
}
