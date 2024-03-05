package com.poorlex.refactoring.battle.battle.domain;

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
public class BattleDuration {

    public static final int START_HOUR = 9;
    public static final int END_HOUR = 22;
    public static final int END_MINUTE = 0;
    public static final int START_MINUTE = 0;
    public static final DayOfWeek START_DAY_OF_WEEK = DayOfWeek.MONDAY;
    public static final DayOfWeek END_DAY_OF_WEEK = DayOfWeek.SUNDAY;
    public static final LocalTime START_TIME = LocalTime.of(START_HOUR, START_MINUTE);
    public static final LocalTime END_TIME = LocalTime.of(END_HOUR, END_MINUTE);
    public static final int BATTLE_DURATION_DAYS = END_DAY_OF_WEEK.getValue() - START_DAY_OF_WEEK.getValue();
    private static final int WEEK_DAYS = DayOfWeek.SUNDAY.compareTo(DayOfWeek.MONDAY) + 1;

    @Column(name = "start_time", updatable = false, nullable = false)
    private LocalDateTime start;
    @Column(name = "end_time", updatable = false, nullable = false)
    private LocalDateTime end;

    BattleDuration(final LocalDateTime start, final LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public static BattleDuration fromDateTime(final LocalDateTime dateTime) {
        final LocalDateTime start = getStartDateTimeBasedOnDateTime(dateTime);
        final LocalDateTime end = getEndDateTimeBasedOnStartDateTime(start);
        return new BattleDuration(start, end);
    }

    private static LocalDateTime getStartDateTimeBasedOnDateTime(final LocalDateTime dateTime) {
        final int numberOfDaysUntilStart = getNumberOfDaysUntilStart(dateTime);
        final LocalDate startDate = LocalDate.from(dateTime).plusDays(numberOfDaysUntilStart);

        return LocalDateTime.of(startDate, START_TIME);
    }

    private static int getNumberOfDaysUntilStart(final LocalDateTime dateTime) {
        if (beforeStartTimeOnStartDay(dateTime)) {
            return 0;
        }
        final int numberOfDaySinceLastStartDate = dateTime.getDayOfWeek().compareTo(START_DAY_OF_WEEK);
        return WEEK_DAYS - numberOfDaySinceLastStartDate;
    }

    private static boolean beforeStartTimeOnStartDay(final LocalDateTime dateTime) {
        return dateTime.getDayOfWeek().equals(START_DAY_OF_WEEK) || dateTime.getHour() < START_HOUR;
    }

    private static LocalDateTime getEndDateTimeBasedOnStartDateTime(final LocalDateTime start) {
        final LocalDate endDateTime = LocalDate.from(start.plusDays(BATTLE_DURATION_DAYS));

        return LocalDateTime.of(endDateTime, END_TIME);
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }
}
