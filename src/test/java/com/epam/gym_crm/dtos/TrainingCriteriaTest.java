package com.epam.gym_crm.dtos;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TrainingCriteriaTest {

    private static final LocalDate EARLY = LocalDate.of(2026, 1, 1);
    private static final LocalDate LATE = LocalDate.of(2026, 12, 31);

    @Test
    void none_hasNoFilters() {
        TraineeTrainingCriteria trainee = TraineeTrainingCriteria.none();
        TrainerTrainingCriteria trainer = TrainerTrainingCriteria.none();

        assertThat(trainee.from()).isNull();
        assertThat(trainee.to()).isNull();
        assertThat(trainee.trainerName()).isNull();
        assertThat(trainee.trainingType()).isNull();
        assertThat(trainer.traineeName()).isNull();
    }

    @Test
    void fromAfterTo_isRejected() {
        assertThatThrownBy(() -> new TraineeTrainingCriteria(LATE, EARLY, null, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new TrainerTrainingCriteria(LATE, EARLY, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void sameDayOrOneSidedRange_isAccepted() {
        assertThat(new TraineeTrainingCriteria(EARLY, EARLY, null, null).from()).isEqualTo(EARLY);
        assertThat(new TraineeTrainingCriteria(EARLY, null, null, null).to()).isNull();
        assertThat(new TrainerTrainingCriteria(null, LATE, null).from()).isNull();
    }
}
