//package com.epam.gym_crm.facade;
//
//import com.epam.gym_crm.dtos.TraineeDTO;
//import com.epam.gym_crm.dtos.TrainerDTO;
//import com.epam.gym_crm.dtos.TrainingDTO;
//import com.epam.gym_crm.entities.Trainee;
//import com.epam.gym_crm.entities.Trainer;
//import com.epam.gym_crm.entities.Training;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.LocalDate;
//import java.util.NoSuchElementException;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
///**
// * Prueba con el contexto real de Spring: verifica el cableado (setters, constructor del facade,
// * qualifiers de los storages) y la carga de data/seed-data.csv.
// * Cada test usa nombres distintos porque el contexto (y los mapas) se comparten entre tests.
// */
//@SpringBootTest
//class GymFacadeIntegrationTest {
//
//    @Autowired
//    private GymFacade facade;
//
//    @Test
//    void seedData_isLoadedAtStartup() {
//        assertThat(facade.getTrainee(1L)).get().extracting(Trainee::getUsername).isEqualTo("John.Smith");
//        assertThat(facade.getTrainer(1L)).get().extracting(Trainer::getUsername).isEqualTo("Ana.Torres");
//        assertThat(facade.getTraining(1L)).isPresent();
//    }
//
//    @Test
//    void createTrainee_withNameAlreadyInSeed_getsSerialSuffix() {
//        TraineeDTO dto = new TraineeDTO();
//        dto.setFirstName("John");
//        dto.setLastName("Smith");
//        dto.setDateOfBirth(LocalDate.of(2000, 1, 15));
//        dto.setAddress("Calle Falsa 123");
//
//        Trainee created = facade.createTrainee(dto);
//
//        assertThat(created.getUsername()).isEqualTo("John.Smith1");
//        assertThat(created.getPassword()).hasSize(10);
//        assertThat(created.getIsActive()).isTrue();
//        assertThat(created.getId()).isGreaterThan(2L);
//        assertThat(facade.getTrainee(created.getId())).containsSame(created);
//    }
//
//    @Test
//    void createTrainer_withNewName_getsPlainUsernameAndWiringWorks() {
//        TrainerDTO dto = new TrainerDTO();
//        dto.setFirstName("Laura");
//        dto.setLastName("Gomez");
//        dto.setSpecialization("Pilates");
//
//        Trainer created = facade.createTrainer(dto);
//
//        assertThat(created.getUsername()).isEqualTo("Laura.Gomez");
//        assertThat(created.getPassword()).hasSize(10);
//        assertThat(created.getIsActive()).isTrue();
//        assertThat(created.getSpecialization().getName()).isEqualTo("Pilates");
//        assertThat(created.getUserId()).isGreaterThan(2L);
//    }
//
//    @Test
//    void createTraining_isStoredWithNextId() {
//        TrainingDTO dto = new TrainingDTO();
//        dto.setTraineeId(1L);
//        dto.setTrainerId(1L);
//        dto.setTrainingName("Integration Training");
//        dto.setTrainingType("Yoga");
//        dto.setTrainingDate(LocalDate.of(2026, 12, 1));
//        dto.setTrainingDuration(30);
//
//        Training created = facade.createTraining(dto);
//
//        assertThat(created.getId()).isGreaterThan(2L);
//        assertThat(facade.getTraining(created.getId())).containsSame(created);
//    }
//
//    @Test
//    void deleteTrainee_removesItFromStorage() {
//        TraineeDTO dto = new TraineeDTO();
//        dto.setFirstName("Pedro");
//        dto.setLastName("Ramirez");
//        dto.setDateOfBirth(LocalDate.of(1990, 5, 5));
//        dto.setAddress("Calle Uno 1");
//        Trainee created = facade.createTrainee(dto);
//
//        assertThat(facade.deleteTrainee(created.getId())).isTrue();
//        assertThat(facade.getTrainee(created.getId())).isEmpty();
//        assertThat(facade.deleteTrainee(created.getId())).isFalse();
//    }
//
//    @Test
//    void updateTrainee_unknownId_throwsNoSuchElementException() {
//        TraineeDTO dto = new TraineeDTO();
//        dto.setFirstName("No");
//        dto.setLastName("Existe");
//
//        assertThatThrownBy(() -> facade.updateTrainee(9999L, dto))
//                .isInstanceOf(NoSuchElementException.class);
//    }
//}
