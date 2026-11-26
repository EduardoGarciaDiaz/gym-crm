package com.epam.gym_crm.repositories;

import com.epam.gym_crm.entities.Training;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class TrainingSpecifications {

    private TrainingSpecifications() {
    }

    public static Specification<Training> forTrainee(String traineeUsername, LocalDate from, LocalDate to,
                                                      String trainerName, String trainingTypeName) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<?, ?> trainee = root.join("trainee");
            Join<?, ?> trainer = root.join("trainer");
            predicates.add(cb.equal(trainee.get("username"), traineeUsername));
            addDateRange(predicates, cb, root.get("trainingDate"), from, to);
            if (hasText(trainerName)) {
                predicates.add(nameLike(cb, trainer, trainerName));
            }
            if (hasText(trainingTypeName)) {
                Join<?, ?> type = root.join("trainingType");
                predicates.add(cb.equal(cb.lower(type.get("name")), trainingTypeName.trim().toLowerCase()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Training> forTrainer(String trainerUsername, LocalDate from, LocalDate to,
                                                     String traineeName) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<?, ?> trainer = root.join("trainer");
            Join<?, ?> trainee = root.join("trainee");
            predicates.add(cb.equal(trainer.get("username"), trainerUsername));
            addDateRange(predicates, cb, root.get("trainingDate"), from, to);
            if (hasText(traineeName)) {
                predicates.add(nameLike(cb, trainee, traineeName));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void addDateRange(List<Predicate> predicates,
                                     jakarta.persistence.criteria.CriteriaBuilder cb,
                                     jakarta.persistence.criteria.Path<LocalDate> date,
                                     LocalDate from, LocalDate to) {
        if (from != null) {
            predicates.add(cb.greaterThanOrEqualTo(date, from));
        }
        if (to != null) {
            predicates.add(cb.lessThanOrEqualTo(date, to));
        }
    }

    private static Predicate nameLike(jakarta.persistence.criteria.CriteriaBuilder cb, Join<?, ?> user, String name) {
        String escaped = name.trim().toLowerCase()
                .replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
        var fullName = cb.lower(cb.concat(cb.concat(user.<String>get("firstName"), " "), user.<String>get("lastName")));
        return cb.like(fullName, "%" + escaped + "%", '\\');
    }

    private static boolean hasText(String s) {
        return s != null && !s.isBlank();
    }
}
