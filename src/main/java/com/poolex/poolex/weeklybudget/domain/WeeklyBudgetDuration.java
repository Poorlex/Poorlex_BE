package com.poolex.poolex.weeklybudget.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeeklyBudgetDuration {

    private static final DayOfWeek START_DAY_OF_WEEK = DayOfWeek.MONDAY;
    private static final DayOfWeek END_DAY_OF_WEEK = DayOfWeek.SUNDAY;
    private static final int WEEKLY_BUDGET_DAYS = END_DAY_OF_WEEK.getValue() - START_DAY_OF_WEEK.getValue();

    @Column(name = "start_date")
    private LocalDate start;
    @Column(name = "end_date")
    private LocalDate end;

    public WeeklyBudgetDuration(final LocalDate start, final LocalDate end) {
        validate(start, end);
        this.start = start;
        this.end = end;
    }

    public static WeeklyBudgetDuration current() {
        final LocalDate now = LocalDate.now();

        final LocalDate start = LocalDate.from(now).plusDays(getDaysBeforeMonday(now));
        final LocalDate end = LocalDate.from(start).plusDays(WEEKLY_BUDGET_DAYS);

        return new WeeklyBudgetDuration(start, end);
    }

    private static int getDaysBeforeMonday(final LocalDate date) {
        final int currentDayOrder = date.getDayOfWeek().getValue();
        return 8 - currentDayOrder;
    }

    private void validate(final LocalDate start, final LocalDate end) {
        validateStart(start);
        validateEnd(end);
    }

    private void validateStart(final LocalDate start) {
        if (start.getDayOfWeek() != START_DAY_OF_WEEK) {
            throw new IllegalArgumentException();
        }
    }

    private void validateEnd(final LocalDate end) {
        if (end.getDayOfWeek() != END_DAY_OF_WEEK) {
            throw new IllegalArgumentException();
        }
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }
}
