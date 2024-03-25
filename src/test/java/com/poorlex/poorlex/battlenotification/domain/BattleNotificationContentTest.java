package com.poorlex.poorlex.battlenotification.domain;

import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BattleNotificationContentTest implements ReplaceUnderScoreTest {

    @ParameterizedTest(name = "공지내용의 길이가 {0} 인 경우")
    @ValueSource(ints = {19, 201})
    void 공지내용의_길이가_20이상_200이하가_아닌_경우_예외를_던진다(final int length) {
        //given
        final String notificationContent = "a".repeat(length);

        //when
        //then
        assertThatThrownBy(() -> new BattleNotificationContent(notificationContent))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 공지내용에는_한글_영어_특수문자_이모티콘을_포함할_수_있다() {
        //given
        final String notificationContent = "abcde가나다라마@#$!^😁😁😁😁😁";

        //when
        final BattleNotificationContent battleNotificationContent = new BattleNotificationContent(notificationContent);

        //then
        assertThat(battleNotificationContent.getValue()).isEqualTo("abcde가나다라마@#$!^😁😁😁😁😁");
    }

    @Test
    void 모두_공백으로만_이루어져_있는_경우_예외를_던진다() {
        //given
        final String notificationContent = " ".repeat(200);

        //when
        //then
        assertThatThrownBy(() -> new BattleNotificationContent(notificationContent))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 앞뒤_공백들은_모두_제거한다() {
        //given
        final String notificationContent = "  1234567890abcdefghij   ";

        //when
        final BattleNotificationContent battleNotificationContent = new BattleNotificationContent(notificationContent);

        //then
        assertThat(battleNotificationContent.getValue()).isEqualTo("1234567890abcdefghij");
    }
}
