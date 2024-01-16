package com.poolex.poolex.expenditure.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.poolex.poolex.expenditure.fixture.ExpenditureFixture;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import com.poolex.poolex.support.UsingDataJpaTest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ExpenditureRepositoryTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private ExpenditureRepository expenditureRepository;

    @Test
    void 멤버의_기간내의_지출의_총합을_구한다() {
        //given
        final Long memberId = 1L;
        final LocalDateTime date = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

        expenditureRepository.save(ExpenditureFixture.simpleWith(1000, memberId, date));
        expenditureRepository.save(ExpenditureFixture.simpleWith(2000, memberId, date));

        //when
        final int sumExpenditure = expenditureRepository.findSumExpenditureByMemberIdAndBetween(
            memberId,
            date,
            date.plusMinutes(1)
        );

        //then
        assertThat(sumExpenditure).isEqualTo(3000);
    }
}
