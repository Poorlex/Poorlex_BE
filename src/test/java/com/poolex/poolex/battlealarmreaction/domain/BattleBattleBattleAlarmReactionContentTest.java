package com.poolex.poolex.battlealarmreaction.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.poolex.poolex.support.ReplaceUnderScoreTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BattleBattleBattleAlarmReactionContentTest implements ReplaceUnderScoreTest {

    @ParameterizedTest(name = "알림반응 문구의 길이가 {0} 인 경우")
    @ValueSource(ints = {1, 31})
    void 알림_반응_문구의_길이가_2이상_30이하가_아닌_경우_예외를_던진다(final int length) {
        //given
        final String battleAlarmReaction = "a".repeat(length);

        //when
        //then
        assertThatThrownBy(() -> new BattleAlarmReactionContent(battleAlarmReaction))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 모두_공백으로만_이루어져_있는_경우_예외를_던진다() {
        //given
        final String battleAlarmReaction = " ".repeat(10);

        //when
        //then
        assertThatThrownBy(() -> new BattleAlarmReactionContent(battleAlarmReaction))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 앞뒤_공백들은_모두_제거한다() {
        //given
        final String battleAlarmReactionValue = "  가나다라마바사  ";

        //when
        final BattleAlarmReactionContent battleAlarmReaction = new BattleAlarmReactionContent(battleAlarmReactionValue);

        //then
        assertThat(battleAlarmReaction.getValue()).isEqualTo("가나다라마바사");
    }
}
