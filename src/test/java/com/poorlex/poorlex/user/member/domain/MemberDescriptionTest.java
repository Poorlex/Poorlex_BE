package com.poorlex.poorlex.user.member.domain;

import com.poorlex.poorlex.exception.ApiException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("회원 소개 테스트")
class MemberDescriptionTest {

    @ParameterizedTest(name = "소개의 길이가 {0} 인 경우")
    @ValueSource(ints = {301})
    void 소개의_길이가_200이하가_아닌_경우_예외를_던진다(final int length) {
        //given
        final String 회원_소개_내용 = "a".repeat(length);

        //when
        //then
        assertThatThrownBy(() -> new MemberDescription(회원_소개_내용))
                .isInstanceOf(ApiException.class);
    }

    @Test
    void 소개에는_한글_영어_특수문자_이모티콘을_포함할_수_있다() {
        //given
        final String 회원_소개_내용 = "a가@#$😁";

        //when
        final MemberDescription 회원_소개 = new MemberDescription(회원_소개_내용);

        //then
        assertThat(회원_소개.getValue()).isEqualTo("a가@#$😁");
    }

    @Test
    void 앞뒤_공백들은_모두_제거한다() {
        //given
        final String 회원_소개_내용 = "  aaa   ";

        //when
        final MemberDescription 회원_소개 = new MemberDescription(회원_소개_내용);

        //then
        assertThat(회원_소개.getValue()).isEqualTo("aaa");
    }
}
