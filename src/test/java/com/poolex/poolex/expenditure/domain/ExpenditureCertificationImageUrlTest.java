package com.poolex.poolex.expenditure.domain;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.poolex.poolex.support.ReplaceUnderScoreTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지출 이미지 테스트")
class ExpenditureCertificationImageUrlTest implements ReplaceUnderScoreTest {

    @Test
    void 포함된_지출이_없는_지출이미지를_생성한다() {
        //given
        final String imageUrlValue = "imageUrl";

        //when
        final ExpenditureCertificationImageUrl imageUrl =
            ExpenditureCertificationImageUrl.withoutIdAndExpenditure(imageUrlValue);

        //then
        assertSoftly(
            softly -> {
                softly.assertThat(imageUrl.getValue()).isEqualTo(imageUrlValue);
                softly.assertThat(imageUrl.getExpenditure()).isNull();
            }
        );
    }
}
