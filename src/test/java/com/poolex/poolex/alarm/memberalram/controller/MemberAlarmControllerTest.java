package com.poolex.poolex.alarm.memberalram.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poolex.poolex.alarm.memberalram.domain.MemberAlarm;
import com.poolex.poolex.alarm.memberalram.domain.MemberAlarmRepository;
import com.poolex.poolex.alarm.memberalram.domain.MemberAlarmType;
import com.poolex.poolex.alarm.memberalram.service.dto.request.MemberAlarmRequest;
import com.poolex.poolex.battle.domain.Battle;
import com.poolex.poolex.battle.domain.BattleRepository;
import com.poolex.poolex.battle.domain.BattleStatus;
import com.poolex.poolex.battle.fixture.BattleFixture;
import com.poolex.poolex.member.domain.Member;
import com.poolex.poolex.member.domain.MemberNickname;
import com.poolex.poolex.member.domain.MemberRepository;
import com.poolex.poolex.participate.domain.BattleParticipant;
import com.poolex.poolex.participate.domain.BattleParticipantRepository;
import com.poolex.poolex.support.IntegrationTest;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import com.poolex.poolex.token.JwtTokenProvider;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

class MemberAlarmControllerTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private BattleParticipantRepository battleParticipantRepository;

    @Autowired
    private MemberAlarmRepository memberAlarmRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 멤버_알림목록을_조회한다() throws Exception {
        //given
        final Member me = createMemberWithOauthId("oauthId1");
        final Member other = createMemberWithOauthId("oauthId2");
        final Battle battle = createBattle();
        final BattleParticipant battleParticipant = join(battle, other);
        final MemberAlarm memberAlarm = memberAlarmRepository.save(
            MemberAlarm.withoutId(
                me.getId(),
                battleParticipant.getId(),
                MemberAlarmType.BATTLE_INVITATION)
        );
        final LocalDateTime requestDateTime = LocalDateTime.now().plusHours(25);
        final MemberAlarmRequest request = new MemberAlarmRequest(requestDateTime);
        final String accessToken = jwtTokenProvider.createAccessToken(me.getId());

        //when
        //then
        mockMvc.perform(
                get("/member/alarms")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].alarmId").value(memberAlarm.getId()))
            .andExpect(jsonPath("$[0].friendName").value(other.getNickname()))
            .andExpect(jsonPath("$[0].battleName").value(battle.getName()))
            .andExpect(jsonPath("$[0].alarmType").value(memberAlarm.getType().name()))
            .andExpect(jsonPath("$[0].minutePassed").value(memberAlarm.getMinutePassed(requestDateTime)))
            .andExpect(jsonPath("$[0].hourPassed").value(memberAlarm.getHourPassed(requestDateTime)))
            .andExpect(jsonPath("$[0].dayPassed").value(memberAlarm.getDayPassed(requestDateTime)));
    }

    private Member createMemberWithOauthId(final String oauthId) {
        final Member member = Member.withoutId(oauthId, new MemberNickname("nickname"));
        return memberRepository.save(member);
    }

    private Battle createBattle() {
        return battleRepository.save(BattleFixture.initialBattleBuilder().status(BattleStatus.PROGRESS).build());
    }

    private BattleParticipant join(final Battle battle, final Member member) {
        return battleParticipantRepository.save(BattleParticipant.normalPlayer(battle.getId(), member.getId()));
    }
}
