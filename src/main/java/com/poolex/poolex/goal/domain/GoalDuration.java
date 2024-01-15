package com.poolex.poolex.goal.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalDuration {

    private static final int MINIMUM_DURATION_DAYS = 1;
    @Column(name = "start_date")
    private LocalDate start;
    @Column(name = "end_date")
    private LocalDate end;

    public GoalDuration(final LocalDate start, final LocalDate end) {
        validate(start, end);
        this.start = start;
        this.end = end;
    }

    private void validate(final LocalDate start, final LocalDate end) {
        validateEndDatNotPassed(end);
        validateEndIsAfterStart(start, end);
    }

    private void validateEndDatNotPassed(final LocalDate end) {
        if (end.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException();
        }
    }

    private void validateEndIsAfterStart(final LocalDate start, final LocalDate end) {
        final long days = ChronoUnit.DAYS.between(start, end);
        if (days < MINIMUM_DURATION_DAYS) {
            throw new IllegalArgumentException();
        }
    }

    public GoalDurationType getType() {
        final long days = ChronoUnit.DAYS.between(start, end);
        return GoalDurationType.findByDays(days)
            .orElseThrow(IllegalArgumentException::new);
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }
}
