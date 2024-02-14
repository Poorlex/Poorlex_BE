package com.poorlex.poorlex.battlealarmreaction.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.poorlex.poorlex.alarm.battlealarm.domain.BattleAlarm;
import com.poorlex.poorlex.alarm.battlealarm.domain.BattleAlarmRepository;
import com.poorlex.poorlex.alarm.battlealarm.domain.BattleAlarmType;
import com.poorlex.poorlex.battlealarmreaction.domain.AlarmReactionRepository;
import com.poorlex.poorlex.battlealarmreaction.service.dto.request.AlarmReactionCreateRequest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.db.UsingDataJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

class BattleAlarmReactionServiceTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private BattleAlarmRepository battleAlarmRepository;

    @Autowired
    private AlarmReactionRepository alarmReactionRepository;

    private AlarmReactionService alarmReactionService;

    @BeforeEach
    void setUp() {
        this.alarmReactionService = new AlarmReactionService(alarmReactionRepository, battleAlarmRepository);
    }

    @ParameterizedTest(name = "알림 타입이 {0} 인 경우")
    @CsvSource(value = {"BATTLE_NOTIFICATION_CHANGED", "EXPENDITURE_NEEDED"})
    void 알림_타입이_공지변경이거나_지출입력인_경우_알림반응을_생성할_수_없다(final BattleAlarmType battleAlarmType) {
        //given
        final long memberId = 1L;
        final BattleAlarm battleAlarm = battleAlarmRepository.save(
            BattleAlarm.withoutId(1L, memberId, battleAlarmType));
        final AlarmReactionCreateRequest request = new AlarmReactionCreateRequest(battleAlarm.getId(), "PRAISE",
            "알림 반응 문구");

        //when
        //then
        assertThatThrownBy(() -> alarmReactionService.createAlarmReaction(memberId, request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "알림 타입이 {0} 인 경우")
    @CsvSource(value = {"EXPENDITURE_CREATED", "OVER_BUDGET", "ZERO_EXPENDITURE"})
    void 알림반응을_생성할_수_있는_알림의_반응을_생성한다(final BattleAlarmType battleAlarmType) {
        //given
        final long memberId = 1L;
        final BattleAlarm battleAlarm = battleAlarmRepository.save(
            BattleAlarm.withoutId(1L, memberId, battleAlarmType));
        final AlarmReactionCreateRequest request = new AlarmReactionCreateRequest(battleAlarm.getId(), "PRAISE",
            "알림 반응 문구");

        //when
        //then
        assertDoesNotThrow(() -> alarmReactionService.createAlarmReaction(memberId, request));
    }
}
