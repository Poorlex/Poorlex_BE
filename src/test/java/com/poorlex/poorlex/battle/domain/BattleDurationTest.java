package com.poorlex.poorlex.battle.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("배틀 기간 테스트")
class BattleDurationTest implements ReplaceUnderScoreTest {

    private static final LocalDateTime VALID_START_TIME = LocalDateTime.of(
        LocalDate.of(2023, 12, 25),
        LocalTime.of(9, 0)
    );
    private static final LocalDateTime VALID_END_TIME = LocalDateTime.of(
        LocalDate.of(2023, 12, 31),
        LocalTime.of(22, 0)
    );

    @Test
    void 배틀의_시작일자와_종료일자를_통해_생성한다() {
        //given
        //when
        final BattleDuration duration = new BattleDuration(VALID_START_TIME, VALID_END_TIME);

        //then
        assertThat(duration.getStart()).isEqualTo(VALID_START_TIME);
        assertThat(duration.getEnd()).isEqualTo(VALID_END_TIME);
    }

    @ParameterizedTest(name = "시작 일자가 {0} 인 경우")
    @MethodSource(value = "getStartTimes")
    void 배틀의_시작이_월요일_9시가_아닐_경우_예외를_던진다(final LocalDateTime startTime) {
        //given
        //when
        //then
        assertThatThrownBy(() -> new BattleDuration(startTime, VALID_END_TIME))
            .isInstanceOf(IllegalArgumentException.class);
    }

    static Stream<LocalDateTime> getStartTimes() {
        final LocalDateTime mondayTen = LocalDateTime.of(2023, 12, 25, 10, 00);
        final LocalDateTime tuesdayNine = LocalDateTime.of(2023, 12, 24, 9, 00);
        return Stream.of(mondayTen, tuesdayNine);
    }

    @ParameterizedTest(name = "종료 일자가 {0} 인 경우")
    @MethodSource(value = "getEndTimes")
    void 배틀의_종료가_일요일_22시가_아닐_경우_예외를_던진다(final LocalDateTime endTime) {
        //given
        //when
        //then
        assertThatThrownBy(() -> new BattleDuration(VALID_START_TIME, endTime))
            .isInstanceOf(IllegalArgumentException.class);
    }

    static Stream<LocalDateTime> getEndTimes() {
        final LocalDateTime sundayTwentyOne = LocalDateTime.of(2023, 12, 31, 21, 00);
        final LocalDateTime saturdayTwentyTwo = LocalDateTime.of(2023, 12, 30, 22, 00);
        return Stream.of(sundayTwentyOne, saturdayTwentyTwo);
    }
}
