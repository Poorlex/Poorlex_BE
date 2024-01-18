package com.poolex.poolex.alarm.battlealarm.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.poolex.poolex.alarm.battlealarm.domain.BattleAlarm;
import com.poolex.poolex.alarm.battlealarm.domain.BattleAlarmRepository;
import com.poolex.poolex.alarm.battlealarm.domain.BattleAlarmType;
import com.poolex.poolex.battle.domain.Battle;
import com.poolex.poolex.battle.domain.BattleRepository;
import com.poolex.poolex.battle.domain.BattleStatus;
import com.poolex.poolex.battle.fixture.BattleFixture;
import com.poolex.poolex.support.IntegrationTest;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class BattleBattleAlarmControllerTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private BattleAlarmRepository battleAlarmRepository;

    @Test
    void 배틀의_알림목록을_조회한다_알림이_있을_때() throws Exception {
        //given
        final Battle battle = createBattle();
        final long memberId = 1L;
        final BattleAlarm battleAlarm = createAlarm(battle, memberId);

        //when
        //then
        mockMvc.perform(
                get("/battles/{battleId}/alarms", battle.getId())
            )
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].alarmId").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].memberId").value(memberId))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].alarmType").value(battleAlarm.getType().name()))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].createdAt").isNotEmpty());
    }

    @Test
    void 배틀의_알림목록을_조회한다_알림이_없을_때() throws Exception {
        //given
        final Battle battle = createBattle();

        //when
        //then
        mockMvc.perform(
                get("/battles/{battleId}/alarms", battle.getId())
            )
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
    }

    private Battle createBattle() {
        return battleRepository.save(BattleFixture.initialBattleBuilder().status(BattleStatus.PROGRESS).build());
    }

    private BattleAlarm createAlarm(final Battle battle, final Long memberId) {
        return battleAlarmRepository.save(
            BattleAlarm.withoutId(battle.getId(), memberId, BattleAlarmType.EXPENDITURE_CREATED));
    }
}
