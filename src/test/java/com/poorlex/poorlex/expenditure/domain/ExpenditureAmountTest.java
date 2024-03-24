package com.poorlex.poorlex.expenditure.domain;

import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("지출 금액 테스트")
class ExpenditureAmountTest implements ReplaceUnderScoreTest {

    @ParameterizedTest(name = "지출 금액이 {0} 인 경우")
    @ValueSource(longs = {-1L, 10_000_000L})
    void 지출금액이_0이상_9_999_999이하가_아닌_경우_예외를_던진다(final long amount) {
        //given
        //when
        //then
        assertThatThrownBy(() -> new ExpenditureAmount(amount))
                .isInstanceOf(ApiException.class);
    }
}
