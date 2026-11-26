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
import java.util.List;
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
        // --- Creación (no requiere credenciales) ---
        Trainer ana = check("01 crear trainer Ana Torres (Yoga)",
                () -> facade.createTrainer(trainer("Ana", "Torres", "Yoga")));
        check("02 crear trainer homónimo -> Ana.Torres1",
                () -> facade.createTrainer(trainer("Ana", "Torres", "Yoga")));
        check("03 crear trainer con tipo inexistente -> NoSuchElementException",
                () -> facade.createTrainer(trainer("Luis", "Perez", "TipoQueNoExiste")));
        Trainee john = check("04 crear trainee SIN fecha ni dirección",
                () -> facade.createTrainee(trainee("John", "Smith", null, null)));
        check("05 trainee con nombre de trainer -> Ana.Torres2",
                () -> facade.createTrainee(trainee("Ana", "Torres", LocalDate.of(1995, 3, 12), "Av. Reforma 12")));
        check("06 trainee con nombre vacío -> ConstraintViolationException",
                () -> facade.createTrainee(trainee("", "Lopez", null, null)));
        if (ana == null || john == null) {
            log.error("No se pudieron crear los datos base; se detiene el escenario.");
            return;
        }

        Credentials johnAuth = new Credentials(john.getUsername(), john.getPassword());
        Credentials anaAuth = new Credentials(ana.getUsername(), ana.getPassword());

        // --- Autenticación (req. 3 y 4) ---
        check("07 match trainee con credenciales correctas -> true", () -> facade.matchTraineeCredentials(johnAuth));
        check("08 match trainee con contraseña incorrecta -> false",
                () -> facade.matchTraineeCredentials(new Credentials(john.getUsername(), "incorrecta")));
        check("09 match trainer con credenciales correctas -> true", () -> facade.matchTrainerCredentials(anaAuth));
        check("10 match trainer con credenciales de trainee -> false", () -> facade.matchTrainerCredentials(johnAuth));
        check("11 getTrainee con credenciales correctas", () -> facade.getTrainee(johnAuth, john.getUsername()).orElseThrow());
        check("12 getTrainee con credenciales nulas -> AuthenticationException", () -> facade.getTrainee(null, john.getUsername()));
        check("13 getTrainee con usuario inexistente -> AuthenticationException",
                () -> facade.getTrainee(new Credentials("No.Existe", "x"), john.getUsername()));
        check("14 getTrainee con credenciales de trainer -> AuthenticationException",
                () -> facade.getTrainee(anaAuth, john.getUsername()));
        check("15 getTrainer con credenciales de trainee -> AuthenticationException",
                () -> facade.getTrainer(johnAuth, ana.getUsername()));

        // --- Actualización y propiedad de la cuenta ---
        check("16 actualizar mi propio trainee", () -> facade.updateTrainee(johnAuth, john.getUsername(),
                trainee("John", "Smith", LocalDate.of(2000, 1, 15), "Calle Nueva 99")));
        check("17 actualizar la cuenta de otro -> SecurityException", () -> facade.updateTrainee(johnAuth, "Ana.Torres2",
                trainee("Hack", "Er", null, null)));
        check("18 actualizar mi trainer", () -> facade.updateTrainer(anaAuth, ana.getUsername(),
                trainer("Ana", "Torres", "Yoga")));

        // --- Trainings ---
        check("19 crear training como trainer", () -> facade.createTraining(anaAuth,
                new TrainingDTO(john.getId(), ana.getId(), "Morning Yoga", "Yoga", LocalDate.of(2026, 12, 1), 60)));
        check("20 crear training con trainee inexistente -> NoSuchElementException", () -> facade.createTraining(anaAuth,
                new TrainingDTO(99999L, ana.getId(), "X", "Yoga", LocalDate.of(2026, 12, 1), 60)));
        check("21 crear training con credenciales malas -> AuthenticationException", () -> facade.createTraining(
                new Credentials(ana.getUsername(), "mala"),
                new TrainingDTO(john.getId(), ana.getId(), "X", "Yoga", LocalDate.of(2026, 12, 1), 60)));

        // --- Estado y contraseña ---
        check("22 desactivar trainee -> false", () -> facade.toggleTraineeStatus(johnAuth));
        check("23 reactivar trainee -> true", () -> facade.toggleTraineeStatus(johnAuth));
        check("24 cambiar contraseña -> true", () -> facade.changeTraineePassword(johnAuth, "NuevaClave123"));
        check("25 credenciales viejas ya no sirven -> AuthenticationException", () -> facade.toggleTraineeStatus(johnAuth));
        Credentials johnNew = new Credentials(john.getUsername(), "NuevaClave123");
        check("26 credenciales nuevas sí sirven", () -> facade.matchTraineeCredentials(johnNew));

        // --- Trainers sin asignar (req. 17) ---
        check("27 trainers sin asignar (activos sin ningún training con este trainee)",
                () -> facade.getUnassignedTrainers(johnNew).stream().map(Trainer::getUsername).toList());
        check("28 Ana.Torres NO debe aparecer: ya tiene un training con John.Smith",
                () -> facade.getUnassignedTrainers(johnNew).stream().noneMatch(t -> t.getUsername().equals(ana.getUsername())));

        // --- Consulta de trainings con criterios (req. 14 y 15) ---
        check("31 trainings del trainee sin filtros -> 1",
                () -> facade.getTraineeTrainings(johnNew, null, null, null, null).size());
        check("32 trainings del trainee, tipo Yoga y trainer 'ana' -> 1",
                () -> facade.getTraineeTrainings(johnNew, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), "ana", "yoga").size());
        check("33 trainings del trainee con rango de fechas sin resultados -> 0",
                () -> facade.getTraineeTrainings(johnNew, LocalDate.of(2020, 1, 1), LocalDate.of(2020, 12, 31), null, null).size());
        check("34 trainings del trainer filtrando por nombre del trainee 'john' -> 1",
                () -> facade.getTrainerTrainings(anaAuth, null, null, "john").size());

        // --- Borrado ---
        check("35 borrar mi trainee (y sus trainings en cascada) -> true",
                () -> facade.deleteTrainee(johnNew, john.getUsername()));
        check("36 borrar de nuevo -> AuthenticationException (ya no existe)",
                () -> facade.deleteTrainee(johnNew, john.getUsername()));
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