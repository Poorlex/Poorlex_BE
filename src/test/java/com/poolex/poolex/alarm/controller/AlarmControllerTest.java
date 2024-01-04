package com.poolex.poolex.alarm.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.poolex.poolex.alarm.domain.Alarm;
import com.poolex.poolex.alarm.domain.AlarmRepository;
import com.poolex.poolex.alarm.domain.AlarmType;
import com.poolex.poolex.battle.domain.Battle;
import com.poolex.poolex.battle.domain.BattleRepository;
import com.poolex.poolex.battle.domain.BattleStatus;
import com.poolex.poolex.battle.fixture.BattleFixture;
import com.poolex.poolex.support.IntegrationTest;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class AlarmControllerTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private AlarmRepository alarmRepository;

    @Test
    void 배틀의_알림목록을_조회한다_알림이_있을_때() throws Exception {
        //given
        final Battle battle = createBattle();
        final long memberId = 1L;
        final Alarm alarm = createAlarm(battle, memberId);

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
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].alarmType").value(alarm.getType().name()))
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

    private Alarm createAlarm(final Battle battle, final Long memberId) {
        return alarmRepository.save(Alarm.withoutId(battle.getId(), memberId, AlarmType.EXPENDITURE_CREATED));
    }
}
