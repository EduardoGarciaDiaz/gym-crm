package com.epam.gym_crm.config;

import com.epam.gym_crm.entities.Trainee;
import com.epam.gym_crm.entities.Trainer;
import com.epam.gym_crm.entities.Training;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StorageInitializerTest {

    private static final String SEED = """
            # comentario que debe ignorarse
            TRAINEE,1,John,Smith,John.Smith,aB3dE5gH7j,true,1995-03-12,Av. Reforma 12
            TRAINEE,2,Maria,Lopez,Maria.Lopez,Zx9Kq2Lm4P,false,1998-07-21,Calle Juarez 45

            TRAINER,1,Ana,Torres,Ana.Torres,Qw8Er5Ty2U,true,Yoga
            TRAINING,1,1,1,Morning Yoga,Yoga,2026-11-20,60
            """;

    @TempDir
    Path tempDir;

    private StorageInitializer initializerFor(Resource resource) {
        StorageInitializer initializer = new StorageInitializer();
        ReflectionTestUtils.setField(initializer, "dataFile", resource);
        return initializer;
    }

    private StorageInitializer initializerWithSeed() throws IOException {
        Path file = tempDir.resolve("seed.csv");
        Files.writeString(file, SEED);
        return initializerFor(new FileSystemResource(file));
    }

    @Test
    void traineeStorage_isFilledOnlyWithTraineeLines() throws IOException {
        Map<Long, Trainee> storage = new HashMap<>();

        Object result = initializerWithSeed().postProcessAfterInitialization(storage, "traineeStorage");

        assertThat(result).isSameAs(storage);
        assertThat(storage).hasSize(2);

        Trainee john = storage.get(1L);
        assertThat(john.getFirstName()).isEqualTo("John");
        assertThat(john.getLastName()).isEqualTo("Smith");
        assertThat(john.getUsername()).isEqualTo("John.Smith");
        assertThat(john.getPassword()).isEqualTo("aB3dE5gH7j");
        assertThat(john.getIsActive()).isTrue();
        assertThat(john.getDateOfBirth()).isEqualTo(LocalDate.of(1995, 3, 12));
        assertThat(john.getAddress()).isEqualTo("Av. Reforma 12");
        assertThat(john.getUserId()).isEqualTo(1L);

        assertThat(storage.get(2L).getIsActive()).isFalse();
    }

    @Test
    void trainerStorage_isFilledOnlyWithTrainerLines() throws IOException {
        Map<Long, Trainer> storage = new HashMap<>();

        initializerWithSeed().postProcessAfterInitialization(storage, "trainerStorage");

        assertThat(storage).hasSize(1);
        Trainer ana = storage.get(1L);
        assertThat(ana.getUsername()).isEqualTo("Ana.Torres");
        assertThat(ana.getSpecialization().getName()).isEqualTo("Yoga");
        assertThat(ana.getUserId()).isEqualTo(1L);
    }

    @Test
    void trainingStorage_isFilledOnlyWithTrainingLines() throws IOException {
        Map<Long, Training> storage = new HashMap<>();

        initializerWithSeed().postProcessAfterInitialization(storage, "trainingStorage");

        assertThat(storage).hasSize(1);
        Training training = storage.get(1L);
        assertThat(training.getId()).isEqualTo(1L);
        assertThat(training.getTraineeId()).isEqualTo(1L);
        assertThat(training.getTrainerId()).isEqualTo(1L);
        assertThat(training.getTrainingName()).isEqualTo("Morning Yoga");
        assertThat(training.getTrainingType().getName()).isEqualTo("Yoga");
        assertThat(training.getTrainingDate()).isEqualTo(LocalDate.of(2026, 11, 20));
        assertThat(training.getTrainingDuration()).isEqualTo(60);
    }

    @Test
    void otherBeans_areReturnedUntouched() throws IOException {
        Object other = new Object();

        Object result = initializerWithSeed().postProcessAfterInitialization(other, "somethingElse");

        assertThat(result).isSameAs(other);
    }

    @Test
    void missingFile_failsFastWithIllegalStateException() {
        StorageInitializer initializer =
                initializerFor(new FileSystemResource(tempDir.resolve("does-not-exist.csv")));

        assertThatThrownBy(() ->
                initializer.postProcessAfterInitialization(new HashMap<Long, Trainee>(), "traineeStorage"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void realSeedFile_loadsTwoOfEachEntity() {
        StorageInitializer initializer = initializerFor(new ClassPathResource("data/seed-data.csv"));
        Map<Long, Trainee> trainees = new HashMap<>();
        Map<Long, Trainer> trainers = new HashMap<>();
        Map<Long, Training> trainings = new HashMap<>();

        initializer.postProcessAfterInitialization(trainees, "traineeStorage");
        initializer.postProcessAfterInitialization(trainers, "trainerStorage");
        initializer.postProcessAfterInitialization(trainings, "trainingStorage");

        assertThat(trainees).hasSize(2);
        assertThat(trainers).hasSize(2);
        assertThat(trainings).hasSize(2);
    }
}
