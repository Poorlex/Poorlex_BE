package com.poolex.poolex.alarm.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.poolex.poolex.alarm.domain.Alarm;
import com.poolex.poolex.alarm.domain.AlarmRepository;
import com.poolex.poolex.alarm.domain.AlarmType;
import com.poolex.poolex.alarm.service.dto.request.BattleAlarmResponse;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import com.poolex.poolex.support.UsingDataJpaTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AlarmServiceTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private AlarmRepository alarmRepository;

    private AlarmService alarmService;

    @BeforeEach
    void setUp() {
        this.alarmService = new AlarmService(alarmRepository);
    }

    @Test
    void 배틀에_포함된_모든_알림을_조회한다_알림이_없을_때() {
        //given
        final long battleId = 1L;

        //when
        final List<BattleAlarmResponse> battleAlarms = alarmService.findBattleAlarms(battleId);

        //then
        assertThat(battleAlarms).isEmpty();
    }

    @Test
    void 배틀에_포함된_모든_알림을_조회한다_알림이_있을_때() {
        //given
        final long battleId = 1L;
        final Alarm alarm1 = createAlarm(battleId, AlarmType.EXPENDITURE_CREATED);
        final Alarm alarm2 = createAlarm(battleId, AlarmType.EXPENDITURE_NEEDED);
        final Alarm alarm3 = createAlarm(battleId, AlarmType.OVER_BUDGET);

        //when
        final List<BattleAlarmResponse> battleAlarms = alarmService.findBattleAlarms(battleId);

        //then
        final List<BattleAlarmResponse> expectedReponse = List.of(
            BattleAlarmResponse.from(alarm1),
            BattleAlarmResponse.from(alarm2),
            BattleAlarmResponse.from(alarm3)
        );
        assertThat(battleAlarms).hasSize(3);
        assertThat(battleAlarms).usingRecursiveComparison().isEqualTo(expectedReponse);
    }

    private Alarm createAlarm(final Long battleId, final AlarmType alarmType) {
        return alarmRepository.save(Alarm.withoutId(battleId, 1L, alarmType));
    }
}
