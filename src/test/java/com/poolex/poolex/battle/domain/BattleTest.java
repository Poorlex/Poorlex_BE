package com.poolex.poolex.battle.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.poolex.poolex.battle.fixture.BattleFixture;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("배틀 테스트")
class BattleTest implements ReplaceUnderScoreTest {

    private static final LocalDateTime VALID_START_TIME = LocalDateTime.of(
        LocalDate.of(2023, 12, 25),
        LocalTime.of(9, 0)
    );
    private static final LocalDateTime VALID_END_TIME = LocalDateTime.of(
        LocalDate.of(2023, 12, 31),
        LocalTime.of(22, 0)
    );

    @Test
    void 배틀을_시작하면_상태가_진행중으로_변경된다() {
        //given
        final Battle battle = BattleFixture.initialBattleBuilder()
            .duration(new BattleDuration(VALID_START_TIME, VALID_END_TIME))
            .build();

        //when
        battle.start(VALID_START_TIME);

        //then
        assertThat(battle.getStatus()).isEqualTo(BattleStatus.PROGRESS);
    }

    @ParameterizedTest(name = "배틀의 상태가 {0}일 경우")
    @CsvSource(value = {"PROGRESS", "COMPLETE"})
    void 배틀_시작시_배틀의_상태가_진행중이거나_완료됨일_경우_에외를_던진다(final BattleStatus invalidStatus) {
        //given
        final Battle battle = BattleFixture.initialBattleBuilder()
            .duration(new BattleDuration(VALID_START_TIME, VALID_END_TIME))
            .status(invalidStatus)
            .build();

        //when
        assertThatThrownBy(() -> battle.start(VALID_START_TIME))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 배틀기간의_시작과_시작시간이_1시간보다_적게_차이가_날_경우_배틀이_시작된다() {
        //given
        final Battle battle = BattleFixture.initialBattleBuilder()
            .duration(new BattleDuration(VALID_START_TIME, VALID_END_TIME))
            .build();
        final LocalDateTime startTime = VALID_START_TIME.plusMinutes(59);

        //when
        //then
        assertDoesNotThrow(() -> battle.start(startTime));
    }

    @Test
    void 배틀기간의_시작과_시작시간이_1시간_이상차이가_날_경우_예외를_던진다() {
        //given
        final Battle battle = BattleFixture.initialBattleBuilder()
            .duration(new BattleDuration(VALID_START_TIME, VALID_END_TIME))
            .build();
        final LocalDateTime startTime = LocalDateTime.from(VALID_START_TIME).plusHours(1);

        //when
        //then
        assertThatThrownBy(() -> battle.start(startTime))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 배틀종료시_상태가_완료됨으로_변경된다() {
        //given
        final Battle battle = BattleFixture.initialBattleBuilder()
            .duration(new BattleDuration(VALID_START_TIME, VALID_END_TIME))
            .status(BattleStatus.PROGRESS)
            .build();

        //when
        battle.end(VALID_END_TIME);

        //then
        assertThat(battle.getStatus()).isEqualTo(BattleStatus.COMPLETE);
    }

    @ParameterizedTest(name = "배틀의 상태가 {0}일 경우")
    @CsvSource(value = {"RECRUITING", "RECRUITING_FINISHED", "COMPLETE"})
    void 배틀_종료시_배틀의_상태가_모집중이거나_모집완료이거_완료됨인_경우_에외를_던진다(final BattleStatus invalidStatus) {
        //given
        final Battle battle = BattleFixture.initialBattleBuilder()
            .duration(new BattleDuration(VALID_START_TIME, VALID_END_TIME))
            .status(invalidStatus)
            .build();

        //when
        assertThatThrownBy(() -> battle.end(VALID_END_TIME))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 배틀기간의_종료와_종료시간이_1시간보다_적게_차이가_날_경우_배틀이_시작된다() {
        //given
        final Battle battle = BattleFixture.initialBattleBuilder()
            .duration(new BattleDuration(VALID_START_TIME, VALID_END_TIME))
            .status(BattleStatus.PROGRESS)
            .build();
        final LocalDateTime endTime = VALID_END_TIME.plusMinutes(59);

        //when
        //then
        assertDoesNotThrow(() -> battle.end(endTime));
    }

    @Test
    void 배틀기간의_종료와_종료시간이_1시간_이상차이가_날_경우_예외를_던진다() {
        //given
        final Battle battle = BattleFixture.initialBattleBuilder()
            .duration(new BattleDuration(VALID_START_TIME, VALID_END_TIME))
            .status(BattleStatus.PROGRESS)
            .build();
        final LocalDateTime endTime = LocalDateTime.from(VALID_END_TIME).plusHours(1);

        //when
        //then
        assertThatThrownBy(() -> battle.start(endTime))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
