package com.epam.gym_crm.controllers;

import com.epam.gym_crm.dtos.CreateUserResponseDTO;
import com.epam.gym_crm.dtos.TraineeDTO;
import com.epam.gym_crm.entities.Trainee;
import com.epam.gym_crm.facade.GymFacade;
import com.epam.gym_crm.services.TraineeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/trainees")
public class TraineeController {

    private TraineeService traineeService;
    private GymFacade gymFacade;

    @Autowired
    public void setTraineeService(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    @Autowired
    public void setGymFacade(GymFacade gymFacade) {
        this.gymFacade = gymFacade;
    }

    @GetMapping()
    public ResponseEntity<Trainee> getTraineeByUsername(@RequestParam String username) {
        Optional<Trainee> trainee = traineeService.getTraineeByUsername(username); //TODO: Use Facade
        if (trainee.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(trainee.get());
        //return trainee.map(
        // value -> ResponseEntity.status(HttpStatus.OK).body(value))
        // .orElseGet(() -> ResponseEntity.notFound().build()
        // );
    }

    @PostMapping()
    public ResponseEntity<CreateUserResponseDTO> createTrainee(@RequestBody @Valid TraineeDTO traineeDTO) {
        CreateUserResponseDTO userCreated = gymFacade.createTrainee(traineeDTO);

        if (userCreated == null) {
            return ResponseEntity.status(500).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteTrainee(@PathVariable String username) {
        if (traineeService.deleteTraineeByUsername(username)) {
            return ResponseEntity.ok("Trainee deleted successfully: " + username);
        }
        return ResponseEntity.status(404).body("Trainee not found: " + username);
    }

    @PutMapping("/{username}")
    public ResponseEntity<Trainee> updateTrainee(@PathVariable String username, @RequestBody @Valid TraineeDTO trainee) {
        Trainee traineeCreated = traineeService.updateTrainee(username, trainee);

        if (traineeCreated == null) {
            return ResponseEntity.status(500).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(traineeCreated);
    }

}
