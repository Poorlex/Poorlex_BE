package com.poolex.poolex.battlealarmreaction.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.poolex.poolex.alarm.battlealarm.domain.BattleAlarm;
import com.poolex.poolex.alarm.battlealarm.domain.BattleAlarmRepository;
import com.poolex.poolex.alarm.battlealarm.domain.BattleAlarmType;
import com.poolex.poolex.battlealarmreaction.domain.BattleAlarmReactionRepository;
import com.poolex.poolex.battlealarmreaction.service.dto.request.BattleAlarmReactionCreateRequest;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import com.poolex.poolex.support.UsingDataJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

class BattleBattleBattleAlarmReactionServiceTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private BattleAlarmRepository battleAlarmRepository;

    @Autowired
    private BattleAlarmReactionRepository battleAlarmReactionRepository;

    private BattleAlarmReactionService battleAlarmReactionService;

    @BeforeEach
    void setUp() {
        this.battleAlarmReactionService = new BattleAlarmReactionService(battleAlarmReactionRepository,
            battleAlarmRepository);
    }

    @ParameterizedTest(name = "알림 타입이 {0} 인 경우")
    @CsvSource(value = {"BATTLE_NOTIFICATION_CHANGED", "EXPENDITURE_NEEDED"})
    void 알림_타입이_공지변경이거나_지출입력인_경우_알림반응을_생성할_수_없다(final BattleAlarmType battleAlarmType) {
        //given
        final long memberId = 1L;
        final BattleAlarm battleAlarm = battleAlarmRepository.save(
            BattleAlarm.withoutId(1L, memberId, battleAlarmType));
        final BattleAlarmReactionCreateRequest request = new BattleAlarmReactionCreateRequest(battleAlarm.getId(),
            "PRAISE",
            "알림 반응 문구");

        //when
        //then
        assertThatThrownBy(() -> battleAlarmReactionService.createAlarmReaction(memberId, request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "알림 타입이 {0} 인 경우")
    @CsvSource(value = {"EXPENDITURE_CREATED", "OVER_BUDGET", "ZERO_EXPENDITURE"})
    void 알림반응을_생성할_수_있는_알림의_반응을_생성한다(final BattleAlarmType battleAlarmType) {
        //given
        final long memberId = 1L;
        final BattleAlarm battleAlarm = battleAlarmRepository.save(
            BattleAlarm.withoutId(1L, memberId, battleAlarmType));
        final BattleAlarmReactionCreateRequest request = new BattleAlarmReactionCreateRequest(battleAlarm.getId(),
            "PRAISE",
            "알림 반응 문구");

        //when
        //then
        assertDoesNotThrow(() -> battleAlarmReactionService.createAlarmReaction(memberId, request));
    }
}
