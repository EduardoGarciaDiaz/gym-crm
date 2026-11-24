package com.epam.gym_crm.repositories;

import com.epam.gym_crm.entities.Trainer;
import com.epam.gym_crm.entities.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;

class TrainerRepositoryTest {

    private Map<Long, Trainer> storage;
    private TrainerRepository repository;

    @BeforeEach
    void setUp() {
        storage = new ConcurrentHashMap<>();
        repository = new TrainerRepository();
        repository.setTrainers(storage);
    }

    private Trainer trainer(String username) {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Ana");
        trainer.setLastName("Torres");
        trainer.setUsername(username);
        trainer.setSpecialization(new TrainingType("Yoga"));
        return trainer;
    }

    @Test
    void saveTrainer_emptyStorage_assignsIdOne() {
        Trainer saved = repository.saveTrainer(trainer("Ana.Torres"));

        assertThat(saved.getUserId()).isEqualTo(1L);
        assertThat(storage).containsEntry(1L, saved);
    }

    @Test
    void saveTrainer_continuesAfterHighestExistingId() {
        Trainer seeded = trainer("Seeded.Trainer");
        seeded.setUserId(2L);
        storage.put(2L, seeded);

        Trainer saved = repository.saveTrainer(trainer("Ana.Torres"));

        assertThat(saved.getUserId()).isEqualTo(3L);
    }

    @Test
    void saveTrainer_withExistingId_replacesWithoutCreatingNewEntry() {
        Trainer saved = repository.saveTrainer(trainer("Ana.Torres"));
        saved.setSpecialization(new TrainingType("Pilates"));

        repository.saveTrainer(saved);

        assertThat(storage).hasSize(1);
        assertThat(storage.get(saved.getUserId()).getSpecialization().getName()).isEqualTo("Pilates");
    }

    @Test
    void findTrainerById_existingId_returnsTrainer() {
        Trainer saved = repository.saveTrainer(trainer("Ana.Torres"));

        assertThat(repository.findTrainerById(saved.getUserId())).containsSame(saved);
    }

    @Test
    void findTrainerById_unknownOrNullId_returnsEmpty() {
        assertThat(repository.findTrainerById(99L)).isEmpty();
        assertThat(repository.findTrainerById(null)).isEmpty();
    }

    @Test
    void existsByUsername_matchesOnlyExactUsername() {
        repository.saveTrainer(trainer("Ana.Torres"));

        assertThat(repository.existsByUsername("Ana.Torres")).isTrue();
        assertThat(repository.existsByUsername("Ana.Torres1")).isFalse();
        assertThat(repository.existsByUsername("Carlos.Ruiz")).isFalse();
    }
}
