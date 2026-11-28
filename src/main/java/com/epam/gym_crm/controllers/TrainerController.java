package com.epam.gym_crm.controllers;

import com.epam.gym_crm.dtos.CreateUserResponseDTO;
import com.epam.gym_crm.dtos.TraineeDTO;
import com.epam.gym_crm.dtos.TrainerDTO;
import com.epam.gym_crm.entities.Trainee;
import com.epam.gym_crm.entities.Trainer;
import com.epam.gym_crm.services.TrainerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/trainers")
public class TrainerController {
    private TrainerService trainerService;

    @Autowired
    public void setTrainerService(TrainerService trainerService) {
        this.trainerService = trainerService;
    }


    @GetMapping
    public ResponseEntity<Trainer> getTrainerByUsername(@RequestParam String username) {
        Optional<Trainer> trainer = trainerService.getTrainerByUsername(username); //TODO: Use Facade

        if (trainer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(trainer.get());
    }

    @PostMapping()
    public ResponseEntity<CreateUserResponseDTO> createTrainer(@RequestBody @Valid TrainerDTO trainerDTO) {
        CreateUserResponseDTO userCreated = trainerService.createTrainer(trainerDTO);

        if (userCreated == null) {
            return ResponseEntity.status(500).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }

    @PutMapping("/{username}")
    public ResponseEntity<Trainer> updateTrainee(@PathVariable String username, @RequestBody @Valid TrainerDTO trainer) {
        Trainer traineeCreated = trainerService.updateTrainer(username, trainer);

        if (traineeCreated == null) {
            return ResponseEntity.status(500).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(traineeCreated);
    }
}
