package com.poolex.poolex.expenditure.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class WeeklyExpenditureDuration {

    private static final DayOfWeek START_DAY_OF_WEEK = DayOfWeek.MONDAY;
    private static final int START_HOUR = 9;
    private static final int START_MINUTE = 0;
    private static final LocalTime START_TIME = LocalTime.of(START_HOUR, START_MINUTE);
    private static final DayOfWeek END_DAY_OF_WEEK = DayOfWeek.SUNDAY;
    private static final int END_HOUR = 22;
    private static final int END_MINUTE = 0;
    private static final LocalTime END_TIME = LocalTime.of(END_HOUR, END_MINUTE);
    private static final int BATTLE_DAYS = END_DAY_OF_WEEK.getValue() - START_DAY_OF_WEEK.getValue();

    private LocalDateTime start;
    private LocalDateTime end;

    private WeeklyExpenditureDuration(final LocalDateTime start, final LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public static WeeklyExpenditureDuration from(final LocalDateTime dateTime) {
        final LocalDate date = LocalDate.from(dateTime);
        final LocalDateTime start = LocalDateTime.of(
            LocalDate.from(date).minusDays(getDaysAfterMonday(date)),
            START_TIME
        );
        final LocalDateTime end = LocalDateTime.of(
            LocalDate.from(start).plusDays(BATTLE_DAYS),
            END_TIME
        );

        return new WeeklyExpenditureDuration(start, end);
    }

    private static int getDaysAfterMonday(final LocalDate date) {
        final int currentDayOrder = date.getDayOfWeek().getValue();
        return currentDayOrder - 1;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }
}
