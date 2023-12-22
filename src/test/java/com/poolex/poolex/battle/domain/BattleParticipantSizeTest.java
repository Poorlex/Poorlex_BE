package com.poolex.poolex.battle.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.poolex.poolex.support.ReplaceUnderScoreTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("배틀 참가자 수 테스트")
class BattleParticipantSizeTest implements ReplaceUnderScoreTest {

    @ParameterizedTest(name = "참여 인원이 {0} 인 경우")
    @ValueSource(ints = {0, 11})
    void 참여_인원이_1이상_10이하가_아닌_경우_예외를_던진다(final int size) {
        //given
        //when
        //then
        assertThatThrownBy(() -> new BattleParticipantSize(size))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
