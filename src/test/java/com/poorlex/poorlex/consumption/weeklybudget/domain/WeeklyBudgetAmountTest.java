package com.poorlex.poorlex.consumption.weeklybudget.domain;

import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class WeeklyBudgetAmountTest implements ReplaceUnderScoreTest {

    @ParameterizedTest(name = "주간예산이 {0}인 경우")
    @ValueSource(longs = {-1, 10_000_000})
    void 주간_예산이_0이상_9_999_999이하면_예외를_던진다(final Long budget) {
        //given
        //when
        //then
        assertThatThrownBy(() -> new WeeklyBudgetAmount(budget))
                .isInstanceOf(ApiException.class);
    }

}
