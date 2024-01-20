package com.poorlex.poorlex.battle.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("배틀 예산 테스트")
class BattleBudgetTest implements ReplaceUnderScoreTest {

    @ParameterizedTest(name = "예산이 {0} 인 경우")
    @ValueSource(ints = {10001, 34879})
    void 예산이_만원단위가_아닌_경우_예외를_던진다(final int budget) {
        //given
        //when
        //then
        assertThatThrownBy(() -> new BattleBudget(budget))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "예산이 {0} 인 경우")
    @ValueSource(ints = {0, 210_000})
    void 예산이_10_000이상_200_000이하가_아닌_경우_예외를_던진다(final int budget) {
        //given
        //when
        //then
        assertThatThrownBy(() -> new BattleBudget(budget))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
