package com.epam.gym_crm;

import com.epam.gym_crm.dtos.*;
import com.epam.gym_crm.entities.*;
import com.epam.gym_crm.facade.GymFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.function.Supplier;

@Component
@Profile("demo")
public class DemoRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoRunner.class);
    private final GymFacade facade;

    public DemoRunner(GymFacade facade) {
        this.facade = facade;
    }

    @Override
    public void run(String... args) {
        Trainer ana = check("01 crear trainer Ana Torres (Yoga)",
                () -> facade.createTrainer(trainer("Ana", "Torres", "Yoga")));
        check("02 crear trainer homónimo -> Ana.Torres1",
                () -> facade.createTrainer(trainer("Ana", "Torres", "Zumba")));
        check("03 crear trainer con tipo inexistente -> error esperado",
                () -> facade.createTrainer(trainer("Luis", "Pena", "Ajedrez")));

        Trainee john = check("04 crear trainee SIN fecha ni dirección (opcionales)",
                () -> facade.createTrainee(trainee("John", "Smith", null, null)));
        check("05 trainee con el nombre de un trainer -> Ana.Torres2",
                () -> facade.createTrainee(trainee("Ana", "Torres", LocalDate.of(1998, 5, 1), "Calle 1")));

        check("06 buscar trainee existente", () -> facade.getTrainee("John.Smith").orElseThrow());
        check("07 buscar trainee inexistente -> false", () -> facade.getTrainee("No.Existe").isPresent());
        check("08 buscar trainer existente", () -> facade.getTrainer("Ana.Torres").orElseThrow());

        if (john != null) {
            check("09 actualizar trainee", () -> facade.updateTrainee(john.getId(),
                    trainee("Johnny", "Smith", LocalDate.of(1995, 3, 12), "Av. Reforma 12")));
        }
        if (john != null && ana != null) {
            check("10 crear training", () -> facade.createTraining(new TrainingDTO(
                    john.getId(), ana.getId(), "Morning Yoga", "Yoga", LocalDate.of(2026, 11, 20), 60)));
            check("11 crear training con trainee inexistente -> error esperado",
                    () -> facade.createTraining(new TrainingDTO(
                            9999L, ana.getId(), "Fantasma", "Yoga", LocalDate.of(2026, 11, 20), 30)));
        }
        check("14 trainee con nombre vacío -> error de validación",
                () -> facade.createTrainee(trainee("", "Smith", null, null)));

        if (john != null) {
            String user = john.getUsername();
            check("15 desactivar trainee -> false", () -> facade.toggleTraineeStatus(user));
            check("16 reactivar trainee -> true", () -> facade.toggleTraineeStatus(user));
            check("17 cambiar contraseña con la actual incorrecta -> false",
                    () -> facade.changeTraineePassword(user, "incorrecta", "NuevaClave123"));
            check("18 cambiar contraseña con la actual correcta -> true",
                    () -> facade.changeTraineePassword(user, john.getPassword(), "NuevaClave123"));
            check("19 estado de usuario inexistente -> error",
                    () -> facade.toggleTraineeStatus("No.Existe"));
        }

        check("12 borrar trainee (y sus trainings en cascada)", () -> facade.deleteTrainee("John.Smith"));
        check("13 borrar trainee inexistente -> false", () -> facade.deleteTrainee("No.Existe"));
        log.info("Escenarios terminados. Verifica los datos con SQL.");
    }

    private <T> T check(String name, Supplier<T> action) {
        try {
            T result = action.get();
            log.info("[OK]   {} -> {}", name, describe(result));
            return result;
        } catch (Exception e) {
            Throwable root = e;
            while (root.getCause() != null) root = root.getCause();
            log.error("[FAIL] {} -> {}: {}", name, e.getClass().getSimpleName(), root.getMessage());
            return null;
        }
    }

    private String describe(Object result) {
        if (result instanceof User u) return "id=" + u.getId() + " username=" + u.getUsername();
        return String.valueOf(result);
    }

    private TrainerDTO trainer(String first, String last, String specialization) {
        TrainerDTO dto = new TrainerDTO();
        dto.setFirstName(first);
        dto.setLastName(last);
        dto.setSpecialization(specialization);
        return dto;
    }

    private TraineeDTO trainee(String first, String last, LocalDate dob, String address) {
        TraineeDTO dto = new TraineeDTO();
        dto.setFirstName(first);
        dto.setLastName(last);
        dto.setDateOfBirth(dob);
        dto.setAddress(address);
        return dto;
    }
}