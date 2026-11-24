package com.epam.gym_crm.repositories;

import com.epam.gym_crm.entities.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;

class TraineeRepositoryTest {

    private Map<Long, Trainee> storage;
    private TraineeRepository repository;

    @BeforeEach
    void setUp() {
        storage = new ConcurrentHashMap<>();
        repository = new TraineeRepository();
        repository.setTrainees(storage);
    }

    private Trainee trainee(String username) {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Smith");
        trainee.setUsername(username);
        return trainee;
    }

    @Test
    void saveTrainee_emptyStorage_assignsIdOne() {
        Trainee saved = repository.saveTrainee(trainee("John.Smith"));

        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(storage).containsEntry(1L, saved);
    }

    @Test
    void saveTrainee_continuesAfterHighestExistingId() {
        Trainee seeded = trainee("Seeded.User");
        seeded.setId(5L);
        storage.put(5L, seeded);

        Trainee saved = repository.saveTrainee(trainee("John.Smith"));

        assertThat(saved.getId()).isEqualTo(6L);
        assertThat(storage).hasSize(2);
    }

    @Test
    void saveTrainee_twoNewTrainees_getDifferentIds() {
        Trainee first = repository.saveTrainee(trainee("John.Smith"));
        Trainee second = repository.saveTrainee(trainee("John.Smith1"));

        assertThat(second.getId()).isNotEqualTo(first.getId());
        assertThat(storage).hasSize(2);
    }

    @Test
    void saveTrainee_withExistingId_replacesWithoutCreatingNewEntry() {
        Trainee saved = repository.saveTrainee(trainee("John.Smith"));
        saved.setAddress("New address");

        repository.saveTrainee(saved);

        assertThat(storage).hasSize(1);
        assertThat(storage.get(saved.getId()).getAddress()).isEqualTo("New address");
    }

    @Test
    void findTraineeById_existingId_returnsTrainee() {
        Trainee saved = repository.saveTrainee(trainee("John.Smith"));

        assertThat(repository.findTraineeById(saved.getId())).containsSame(saved);
    }

    @Test
    void findTraineeById_unknownId_returnsEmpty() {
        assertThat(repository.findTraineeById(99L)).isEmpty();
    }

    @Test
    void findTraineeById_nullId_returnsEmpty() {
        assertThat(repository.findTraineeById(null)).isEmpty();
    }

    @Test
    void deleteTraineeById_existingId_removesAndReturnsTrue() {
        Trainee saved = repository.saveTrainee(trainee("John.Smith"));

        assertThat(repository.deleteTraineeById(saved.getId())).isTrue();
        assertThat(storage).isEmpty();
    }

    @Test
    void deleteTraineeById_unknownId_returnsFalse() {
        assertThat(repository.deleteTraineeById(99L)).isFalse();
    }

    @Test
    void deleteTraineeById_nullId_returnsFalse() {
        assertThat(repository.deleteTraineeById(null)).isFalse();
    }

    @Test
    void existsByUsername_matchesOnlyExactUsername() {
        repository.saveTrainee(trainee("John.Smith"));

        assertThat(repository.existsByUsername("John.Smith")).isTrue();
        assertThat(repository.existsByUsername("John.Smith1")).isFalse();
        assertThat(repository.existsByUsername("Maria.Lopez")).isFalse();
    }
}
