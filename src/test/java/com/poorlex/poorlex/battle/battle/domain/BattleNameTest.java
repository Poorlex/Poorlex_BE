package com.poorlex.poorlex.battle.battle.domain;

import com.poorlex.poorlex.battle.battle.domain.BattleName;
import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("배틀 이름 테스트")
class BattleNameTest implements ReplaceUnderScoreTest {

    @ParameterizedTest(name = "이름의 길이가 {0} 인 경우")
    @ValueSource(ints = {1, 13})
    void 이름의_길이가_2_이상_12_이하가_아닌_경우_예외를_던진다(final int length) {
        //given
        final String name = "a".repeat(length);

        //when
        //then
        assertThatThrownBy(() -> new BattleName(name))
                .isInstanceOf(ApiException.class);
    }

    @Test
    void 이름에는_한글_영어_특수문자_이모티콘을_포함할_수_있다() {
        //given
        final String name = "a가@#$😁";

        //when
        final BattleName battleName = new BattleName(name);

        //then
        assertThat(battleName.getValue()).isEqualTo("a가@#$😁");
    }

    @Test
    void 모두_공백으로만_이루어져_있는_경우_예외를_던진다() {
        //given
        final String name = " ".repeat(12);

        //when
        //then
        assertThatThrownBy(() -> new BattleName(name))
                .isInstanceOf(ApiException.class);
    }

    @Test
    void 앞뒤_공백들은_모두_제거한다() {
        //given
        final String name = "  aaa   ";

        //when
        final BattleName battleName = new BattleName(name);

        //then
        assertThat(battleName.getValue()).isEqualTo("aaa");
    }
}
