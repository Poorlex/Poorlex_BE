package com.poorlex.poorlex.consumption.expenditure.domain;

import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("지출 설명 테스트")
class ExpenditureDescriptionTest implements ReplaceUnderScoreTest {

    @ParameterizedTest(name = "지출설명의 길이가 {0} 인 경우")
    @ValueSource(ints = {0, 31})
    void 지출설명의_길이가_1이상_30이하가_아닌_경우_예외를_던진다(final int length) {
        //given
        final String introduction = "a".repeat(length);

        //when
        //then
        assertThatThrownBy(() -> new ExpenditureDescription(introduction))
                .isInstanceOf(ApiException.class);
    }

    @Test
    void 지출설명에는_한글_영어_특수문자_이모티콘을_포함할_수_있다() {
        //given
        final String name = "a가@#$😁";

        //when
        final ExpenditureDescription expenditureDescription = new ExpenditureDescription(name);

        //then
        assertThat(expenditureDescription.getValue()).isEqualTo("a가@#$😁");
    }

    @Test
    void 모두_공백으로만_이루어져_있는_경우_예외를_던진다() {
        //given
        final String description = " ".repeat(200);

        //when
        //then
        assertThatThrownBy(() -> new ExpenditureDescription(description))
                .isInstanceOf(ApiException.class);
    }

    @Test
    void 앞뒤_공백들은_모두_제거한다() {
        //given
        final String description = "  aaa   ";

        //when
        final ExpenditureDescription expenditureDescription = new ExpenditureDescription(description);

        //then
        assertThat(expenditureDescription.getValue()).isEqualTo("aaa");
    }
}
