package com.poolex.poolex.alarmreaction.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.poolex.poolex.support.ReplaceUnderScoreTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AlarmReactionContentTest implements ReplaceUnderScoreTest {

    @ParameterizedTest(name = "알림반응 문구의 길이가 {0} 인 경우")
    @ValueSource(ints = {0, 11})
    void 알림_반응_문구의_길이가_1이상_10이하가_아닌_경우_예외를_던진다(final int length) {
        //given
        final String alarmReactionContent = "a".repeat(length);

        //when
        //then
        assertThatThrownBy(() -> new AlarmReactionContent(alarmReactionContent))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "알림 반응 문구가 {0} 일 때")
    @ValueSource(strings = {"abcde", "12345", "😁😁😁😁😁"})
    void 알림_반응_문구에_한글이_아닌_문자가_포함되면_예외를_던진다(final String invalidContent) {
        //given
        //when
        //then
        assertThatThrownBy(() -> new AlarmReactionContent(invalidContent))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 모두_공백으로만_이루어져_있는_경우_예외를_던진다() {
        //given
        final String alarmReactionContent = " ".repeat(10);

        //when
        //then
        assertThatThrownBy(() -> new AlarmReactionContent(alarmReactionContent))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 앞뒤_공백들은_모두_제거한다() {
        //given
        final String alarmReactionContentValue = "  가나다라마바사  ";

        //when
        final AlarmReactionContent alarmReactionContent = new AlarmReactionContent(alarmReactionContentValue);

        //then
        assertThat(alarmReactionContent.getValue()).isEqualTo("가나다라마바사");
    }
}
