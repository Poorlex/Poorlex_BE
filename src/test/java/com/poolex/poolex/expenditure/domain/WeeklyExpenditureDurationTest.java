package com.poolex.poolex.expenditure.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.poolex.poolex.support.ReplaceUnderScoreTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class WeeklyExpenditureDurationTest implements ReplaceUnderScoreTest {

    @Test
    void 날짜를_기준으로_지출_주간_기간을_생성한다() {
        //given
        final LocalDate januaryThird = LocalDate.of(2024, 1, 3);

        //when
        final WeeklyExpenditureDuration duration = WeeklyExpenditureDuration.from(januaryThird);

        //then
        final LocalDateTime expectedStart = LocalDateTime.of(2024, 1, 1, 9, 0);
        final LocalDateTime expectedEnd = LocalDateTime.of(2024, 1, 7, 22, 0);

        assertThat(duration.getStart()).isEqualTo(expectedStart);
        assertThat(duration.getEnd()).isEqualTo(expectedEnd);
    }
}
