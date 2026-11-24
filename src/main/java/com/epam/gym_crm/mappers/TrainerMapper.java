package com.epam.gym_crm.mappers;

import com.epam.gym_crm.dtos.TraineeDTO;
import com.epam.gym_crm.dtos.TrainerDTO;
import com.epam.gym_crm.entities.Trainee;
import com.epam.gym_crm.entities.Trainer;
import com.epam.gym_crm.entities.TrainingType;
import org.springframework.stereotype.Component;

@Component
public class TrainerMapper {
    public Trainer dtoToTrainer(TrainerDTO dto) {
        if (dto == null) {
            throw new NullPointerException("TrainerDTO can not be null");
        }

        Trainer trainer = new Trainer();
        trainer.setFirstName(dto.getFirstName());
        trainer.setLastName(dto.getLastName());

        return trainer;
    }

    public void updateTrainerFromDTO(TrainerDTO dto, Trainer trainer) {
        if (dto == null || trainer == null) {
            throw new IllegalArgumentException("Arguments TrainerDTO and Trainer cannot be null");
        }

        trainer.setFirstName(dto.getFirstName());
        trainer.setLastName(dto.getLastName());
    }
}
