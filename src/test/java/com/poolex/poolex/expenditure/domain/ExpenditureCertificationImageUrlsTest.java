package com.poolex.poolex.expenditure.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.poolex.poolex.support.ReplaceUnderScoreTest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("지출 이미지 목록 테스트")
class ExpenditureCertificationImageUrlsTest implements ReplaceUnderScoreTest {

    @ParameterizedTest(name = "지출 이미지URL가 {0}개 인 경우")
    @ValueSource(ints = {0, 3})
    void 지출_이미지URL의_갯수가_1개이상_2개이하가_아닐경우_예외를_던진다(final int size) {
        //given
        final List<ExpenditureCertificationImageUrl> imageUrlList = IntStream.range(0, size)
            .mapToObj(count -> ExpenditureCertificationImageUrl.withoutIdAndExpenditure("imageUrl"))
            .toList();

        //when
        //then
        assertThatThrownBy(() -> new ExpenditureCertificationImageUrls(imageUrlList))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 지출_이미지들이_포함된_지출을_등록한다() {
        //given
        final ExpenditureCertificationImageUrls imageUrls = new ExpenditureCertificationImageUrls(List.of(
            ExpenditureCertificationImageUrl.withoutIdAndExpenditure("imageUrl1"),
            ExpenditureCertificationImageUrl.withoutIdAndExpenditure("imageUrl2")
        ));
        final Expenditure expenditure = simpleWithoutId(imageUrls);

        //when
        imageUrls.belongTo(expenditure);

        //then
        assertThat(imageUrls.getImageUrls())
            .map(ExpenditureCertificationImageUrl::getExpenditure)
            .containsOnly(expenditure);
    }

    private Expenditure simpleWithoutId(final ExpenditureCertificationImageUrls imageUrls) {
        final long memberId = 1L;
        final ExpenditureAmount amount = new ExpenditureAmount(0L);
        final LocalDateTime date = LocalDateTime.now();
        final ExpenditureDescription description = new ExpenditureDescription("description");

        return new Expenditure(null, memberId, amount, date, description, imageUrls);
    }
}
