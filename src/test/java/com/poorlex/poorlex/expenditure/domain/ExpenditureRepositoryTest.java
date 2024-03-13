package com.poorlex.poorlex.expenditure.domain;

import com.poorlex.poorlex.expenditure.fixture.ExpenditureFixture;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.db.UsingDataJpaTest;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ExpenditureRepositoryTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private ExpenditureRepository expenditureRepository;

    @Test
    void 멤버의_기간내의_지출의_총합을_구한다() {
        //given
        final Long memberId = 1L;
        final LocalDate date = LocalDate.now();

        expenditureRepository.save(ExpenditureFixture.simpleWithMainImage(1000, memberId, date));
        expenditureRepository.save(ExpenditureFixture.simpleWithMainImage(2000, memberId, date));

        //when
        final int sumExpenditure = expenditureRepository.findSumExpenditureByMemberIdAndBetween(
                memberId,
                date.minusDays(1),
                date
        );

        //then
        assertThat(sumExpenditure).isEqualTo(3000);
    }
}
