package com.epam.gym_crm.repositories;

import com.epam.gym_crm.entities.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingRepositoryTest {

    private Map<Long, Training> storage;
    private TrainingRepository repository;

    @BeforeEach
    void setUp() {
        storage = new ConcurrentHashMap<>();
        repository = new TrainingRepository();
        repository.setTrainings(storage);
    }

    private Training training(String name) {
        Training training = new Training();
        training.setTrainingName(name);
        return training;
    }

    @Test
    void saveTraining_emptyStorage_assignsIdOne() {
        Training saved = repository.saveTraining(training("Morning Yoga"));

        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(storage).containsEntry(1L, saved);
    }

    @Test
    void saveTraining_continuesAfterHighestExistingId() {
        Training seeded = training("Seeded");
        seeded.setId(2L);
        storage.put(2L, seeded);

        Training saved = repository.saveTraining(training("Evening Cardio"));

        assertThat(saved.getId()).isEqualTo(3L);
    }

    @Test
    void saveTraining_twoNewTrainings_getDifferentIds() {
        Training first = repository.saveTraining(training("A"));
        Training second = repository.saveTraining(training("B"));

        assertThat(second.getId()).isNotEqualTo(first.getId());
        assertThat(storage).hasSize(2);
    }

    @Test
    void findTrainingById_existingId_returnsTraining() {
        Training saved = repository.saveTraining(training("Morning Yoga"));

        assertThat(repository.findTrainingById(saved.getId())).containsSame(saved);
    }

    @Test
    void findTrainingById_unknownOrNullId_returnsEmpty() {
        assertThat(repository.findTrainingById(99L)).isEmpty();
        assertThat(repository.findTrainingById(null)).isEmpty();
    }
}
