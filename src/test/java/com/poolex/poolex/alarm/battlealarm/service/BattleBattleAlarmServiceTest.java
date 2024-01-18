package com.poolex.poolex.alarm.battlealarm.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.poolex.poolex.alarm.battlealarm.domain.BattleAlarm;
import com.poolex.poolex.alarm.battlealarm.domain.BattleAlarmRepository;
import com.poolex.poolex.alarm.battlealarm.domain.BattleAlarmType;
import com.poolex.poolex.alarm.battlealarm.service.dto.response.BattleAlarmResponse;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import com.poolex.poolex.support.UsingDataJpaTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BattleBattleAlarmServiceTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private BattleAlarmRepository battleAlarmRepository;

    private BattleAlarmService battleAlarmService;

    @BeforeEach
    void setUp() {
        this.battleAlarmService = new BattleAlarmService(battleAlarmRepository);
    }

    @Test
    void 배틀에_포함된_모든_알림을_조회한다_알림이_없을_때() {
        //given
        final long battleId = 1L;

        //when
        final List<BattleAlarmResponse> battleAlarms = battleAlarmService.findBattleAlarms(battleId);

        //then
        assertThat(battleAlarms).isEmpty();
    }

    @Test
    void 배틀에_포함된_모든_알림을_조회한다_알림이_있을_때() {
        //given
        final long battleId = 1L;
        final BattleAlarm battleAlarm1 = createAlarm(battleId, BattleAlarmType.EXPENDITURE_CREATED);
        final BattleAlarm battleAlarm2 = createAlarm(battleId, BattleAlarmType.EXPENDITURE_NEEDED);
        final BattleAlarm battleAlarm3 = createAlarm(battleId, BattleAlarmType.OVER_BUDGET);

        //when
        final List<BattleAlarmResponse> battleAlarms = battleAlarmService.findBattleAlarms(battleId);

        //then
        final List<BattleAlarmResponse> expectedReponse = List.of(
            BattleAlarmResponse.from(battleAlarm1),
            BattleAlarmResponse.from(battleAlarm2),
            BattleAlarmResponse.from(battleAlarm3)
        );
        assertThat(battleAlarms).hasSize(3);
        assertThat(battleAlarms).usingRecursiveComparison().isEqualTo(expectedReponse);
    }

    private BattleAlarm createAlarm(final Long battleId, final BattleAlarmType battleAlarmType) {
        return battleAlarmRepository.save(BattleAlarm.withoutId(battleId, 1L, battleAlarmType));
    }
}
