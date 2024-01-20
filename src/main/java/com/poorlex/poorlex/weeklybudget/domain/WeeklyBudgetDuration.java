package com.poorlex.poorlex.weeklybudget.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeeklyBudgetDuration {

    private static final DayOfWeek START_DAY_OF_WEEK = DayOfWeek.MONDAY;
    private static final int START_HOUR = 9;
    private static final int START_MINUTE = 0;
    private static final LocalTime START_TIME = LocalTime.of(START_HOUR, START_MINUTE);
    private static final DayOfWeek END_DAY_OF_WEEK = DayOfWeek.SUNDAY;
    private static final int END_HOUR = 22;
    private static final int END_MINUTE = 0;
    private static final LocalTime END_TIME = LocalTime.of(END_HOUR, END_MINUTE);
    private static final int BATTLE_DAYS = END_DAY_OF_WEEK.getValue() - START_DAY_OF_WEEK.getValue();

    @Column(name = "start_date")
    private LocalDateTime start;
    @Column(name = "end_date")
    private LocalDateTime end;

    public WeeklyBudgetDuration(final LocalDateTime start, final LocalDateTime end) {
        validate(start, end);
        this.start = start;
        this.end = end;
    }

    public static WeeklyBudgetDuration current() {
        final LocalDate now = LocalDate.now();

        final LocalDateTime start = LocalDateTime.of(
            LocalDate.from(now).plusDays(getDaysBeforeMonday(now)),
            START_TIME
        );
        final LocalDateTime end = LocalDateTime.of(
            LocalDate.from(start).plusDays(BATTLE_DAYS),
            END_TIME
        );

        return new WeeklyBudgetDuration(start, end);
    }

    private static int getDaysBeforeMonday(final LocalDate date) {
        final int currentDayOrder = date.getDayOfWeek().getValue();
        return 8 - currentDayOrder;
    }

    private void validate(final LocalDateTime start, final LocalDateTime end) {
        validateStart(start);
        validateEnd(end);
    }

    private void validateStart(final LocalDateTime start) {
        final DayOfWeek dayOfWeek = start.getDayOfWeek();
        final int hour = start.getHour();
        final int minute = start.getMinute();

        if (dayOfWeek != START_DAY_OF_WEEK || hour != START_HOUR || minute != START_MINUTE) {
            throw new IllegalArgumentException();
        }
    }

    private void validateEnd(final LocalDateTime end) {
        final DayOfWeek dayOfWeek = end.getDayOfWeek();
        final int hour = end.getHour();
        final int minute = end.getMinute();

        if (dayOfWeek != END_DAY_OF_WEEK || hour != END_HOUR || minute != END_MINUTE) {
            throw new IllegalArgumentException();
        }
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }
}
