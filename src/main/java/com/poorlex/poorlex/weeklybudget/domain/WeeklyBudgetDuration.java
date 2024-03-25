package com.poorlex.poorlex.weeklybudget.domain;

import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.exception.ExceptionTag;
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
    private static final int BATTLE_DAYS = END_DAY_OF_WEEK.getValue() - START_DAY_OF_WEEK.getValue();

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
        final LocalDate end = start.plusDays(BATTLE_DAYS);

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
        final DayOfWeek dayOfWeek = start.getDayOfWeek();
        if (dayOfWeek != START_DAY_OF_WEEK) {
            final String errorMessage = String.format("주간 예산 기간의 시작 요일은 %s 입니다. ( 현재 시작 요일 : %s )",
                                                      START_DAY_OF_WEEK.name(),
                                                      dayOfWeek.name());
            throw new ApiException(ExceptionTag.WEEKLY_BUDGET_DURATION, errorMessage);
        }
    }

    private void validateEnd(final LocalDate end) {
        final DayOfWeek dayOfWeek = end.getDayOfWeek();
        if (dayOfWeek != END_DAY_OF_WEEK) {
            final String errorMessage = String.format("주간 예산 기간의 종료 요일은 %s 입니다. ( 현재 시작 요일 : %s )",
                                                      END_DAY_OF_WEEK.name(),
                                                      dayOfWeek.name());
            throw new ApiException(ExceptionTag.WEEKLY_BUDGET_DURATION, errorMessage);
        }
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }
}
