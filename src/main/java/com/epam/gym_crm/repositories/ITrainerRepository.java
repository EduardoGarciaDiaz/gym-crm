package com.epam.gym_crm.repositories;

import com.epam.gym_crm.entities.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ITrainerRepository extends JpaRepository<Trainer, Long> {
    boolean existsByUsername(String username);

    Optional<Trainer> findByUsername(String username);

    /** Trainers activos con los que el trainee todavía no tiene ningún training. */
    @Query("""
            select t from Trainer t
            where t.isActive = true
              and not exists (
                  select 1 from Training tr
                  where tr.trainer = t and tr.trainee.username = :username
              )
            order by t.username
            """)
    List<Trainer> findActiveNotAssignedToTrainee(@Param("username") String username);
}
