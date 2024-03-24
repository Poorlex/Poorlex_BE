package com.poorlex.poorlex.weeklybudget.domain;

import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;

class WeeklyBudgetDurationTest implements ReplaceUnderScoreTest {

    private static final LocalDate VALID_WEEKLY_BUDGET_DURATION_START = LocalDate.of(2023, 12, 25);
    private static final LocalDate VALID_WEEKLY_BUDGET_DURATION_END = LocalDate.of(2023, 12, 31);

    @Test
    void 주간_예산의_기간을_생성한다() {
        //given
        //when
        //then
        assertDoesNotThrow(() -> new WeeklyBudgetDuration(
                VALID_WEEKLY_BUDGET_DURATION_START,
                VALID_WEEKLY_BUDGET_DURATION_END)
        );
    }

    @Test
    void 주간_예산의_기간의_시작이_월요일이_아닐_경우_예외를_던진다() {
        //given
        final LocalDate thursday = LocalDate.of(2023, 12, 28);

        //when
        //then
        assertThatThrownBy(() -> new WeeklyBudgetDuration(thursday, VALID_WEEKLY_BUDGET_DURATION_END))
                .isInstanceOf(ApiException.class);
    }

    @Test
    void 주간_예산의_기간의_끝이_일요일이_아닐_경우_예외를_던진다() {
        //given
        final LocalDate thursday = LocalDate.of(2023, 12, 28);

        //when
        //then
        assertThatThrownBy(() -> new WeeklyBudgetDuration(VALID_WEEKLY_BUDGET_DURATION_START, thursday))
                .isInstanceOf(ApiException.class);
    }
}
