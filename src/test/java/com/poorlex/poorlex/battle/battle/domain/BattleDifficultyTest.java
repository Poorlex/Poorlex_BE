package com.poorlex.poorlex.battle.battle.domain;

import com.poorlex.poorlex.battle.battle.domain.BattleBudget;
import com.poorlex.poorlex.battle.battle.domain.BattleDifficulty;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("배틀 난이도 테스트")
class BattleDifficultyTest implements ReplaceUnderScoreTest {

    @ParameterizedTest(name = "예산이 {0} 일 경우 난이도는 {1} 이다. ")
    @CsvSource(value = {
            "10000:HARD", "20000:HARD", "30000:HARD", "40000:HARD", "50000:HARD", "60000:HARD", "70000:HARD",
            "80000:HARD",
            "90000:NORMAL", "100000:NORMAL", "110000:NORMAL", "120000:NORMAL", "130000:NORMAL", "140000:NORMAL",
            "150000:EASY", "160000:EASY", "170000:EASY", "180000:EASY", "190000:EASY", "200000:EASY"
    }, delimiter = ':')
    void 예산에_따라_난이도가_결정된다(final int budget, final BattleDifficulty expectedDifficulty) {
        //given
        final BattleBudget battleBudget = new BattleBudget(budget);

        //when
        final Optional<BattleDifficulty> findDifficulty = BattleDifficulty.findByBattleBudget(battleBudget);

        //then
        assertThat(findDifficulty).isPresent()
                .contains(expectedDifficulty);
    }
}
