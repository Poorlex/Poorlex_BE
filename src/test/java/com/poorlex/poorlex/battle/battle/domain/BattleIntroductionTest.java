package com.poorlex.poorlex.battle.battle.domain;

import com.poorlex.poorlex.battle.battle.domain.BattleIntroduction;
import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("배틀 소개글 테스트")
class BattleIntroductionTest implements ReplaceUnderScoreTest {

    @ParameterizedTest(name = "소개의 길이가 {0} 인 경우")
    @ValueSource(ints = {1, 201})
    void 소개의_길이가_2이상_200이하가_아닌_경우_예외를_던진다(final int length) {
        //given
        final String introduction = "a".repeat(length);

        //when
        //then
        assertThatThrownBy(() -> new BattleIntroduction(introduction))
                .isInstanceOf(ApiException.class);
    }

    @Test
    void 소개에는_한글_영어_특수문자_이모티콘을_포함할_수_있다() {
        //given
        final String introduction = "a가@#$😁";

        //when
        final BattleIntroduction battleIntroduction = new BattleIntroduction(introduction);

        //then
        assertThat(battleIntroduction.getValue()).isEqualTo("a가@#$😁");
    }

    @Test
    void 모두_공백으로만_이루어져_있는_경우_예외를_던진다() {
        //given
        final String introduction = " ".repeat(200);

        //when
        //then
        assertThatThrownBy(() -> new BattleIntroduction(introduction))
                .isInstanceOf(ApiException.class);
    }

    @Test
    void 앞뒤_공백들은_모두_제거한다() {
        //given
        final String introduction = "  aaa   ";

        //when
        final BattleIntroduction battleIntroduction = new BattleIntroduction(introduction);

        //then
        assertThat(battleIntroduction.getValue()).isEqualTo("aaa");
    }
}
