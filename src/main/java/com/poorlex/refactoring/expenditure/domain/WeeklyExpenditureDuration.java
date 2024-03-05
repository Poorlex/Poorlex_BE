package com.poorlex.refactoring.expenditure.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class WeeklyExpenditureDuration {

    private static final DayOfWeek START_DAY_OF_WEEK = DayOfWeek.MONDAY;
    private static final DayOfWeek END_DAY_OF_WEEK = DayOfWeek.SUNDAY;
    private static final int BATTLE_DAYS = END_DAY_OF_WEEK.getValue() - START_DAY_OF_WEEK.getValue();

    private LocalDate start;
    private LocalDate end;

    private WeeklyExpenditureDuration(final LocalDate start, final LocalDate end) {
        this.start = start;
        this.end = end;
    }

    public static WeeklyExpenditureDuration from(final LocalDate date) {
        final LocalDate start = date.minusDays(getDaysAfterMonday(date));
        final LocalDate end = start.plusDays(BATTLE_DAYS);

        return new WeeklyExpenditureDuration(start, end);
    }

    private static int getDaysAfterMonday(final LocalDate date) {
        final int currentDayOrder = date.getDayOfWeek().getValue();
        return currentDayOrder - 1;
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }
}
