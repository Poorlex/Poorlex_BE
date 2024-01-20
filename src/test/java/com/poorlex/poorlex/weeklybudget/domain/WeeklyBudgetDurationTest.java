package com.poorlex.poorlex.weeklybudget.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class WeeklyBudgetDurationTest implements ReplaceUnderScoreTest {

    private static final LocalDateTime VALID_WEEKLY_BUDGET_DURATION_START = LocalDateTime.of(2023, 12, 25, 9, 0);
    private static final LocalDateTime VALID_WEEKLY_BUDGET_DURATION_END = LocalDateTime.of(2023, 12, 31, 22, 0);

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
        final LocalDateTime thursday = LocalDateTime.of(2023, 12, 28, 9, 0);

        //when
        //then
        assertThatThrownBy(() -> new WeeklyBudgetDuration(thursday, VALID_WEEKLY_BUDGET_DURATION_END))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주간_예산의_기간의_끝이_일요일이_아닐_경우_예외를_던진다() {
        //given
        final LocalDateTime thursday = LocalDateTime.of(2023, 12, 28, 22, 0);

        //when
        //then
        assertThatThrownBy(() -> new WeeklyBudgetDuration(VALID_WEEKLY_BUDGET_DURATION_START, thursday))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
