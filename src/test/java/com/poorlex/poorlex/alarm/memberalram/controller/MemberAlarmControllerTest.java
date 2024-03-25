package com.poorlex.poorlex.alarm.memberalram.controller;

import com.poorlex.poorlex.alarm.memberalram.domain.MemberAlarm;
import com.poorlex.poorlex.alarm.memberalram.domain.MemberAlarmRepository;
import com.poorlex.poorlex.alarm.memberalram.domain.MemberAlarmType;
import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleRepository;
import com.poorlex.poorlex.battle.domain.BattleStatus;
import com.poorlex.poorlex.battle.fixture.BattleFixture;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberNickname;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId;
import com.poorlex.poorlex.participate.domain.BattleParticipant;
import com.poorlex.poorlex.participate.domain.BattleParticipantRepository;
import com.poorlex.poorlex.support.IntegrationTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.token.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                        MemberAlarmType.BATTLE_INVITATION_NOT_ACCEPTED)
        );
        final String accessToken = jwtTokenProvider.createAccessToken(me.getId());

        //when
        //then
        mockMvc.perform(
                        get("/member/alarms")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].alarmId").value(memberAlarm.getId()))
                .andExpect(jsonPath("$[0].friendName").value(other.getNickname()))
                .andExpect(jsonPath("$[0].battleName").value(battle.getName()))
                .andExpect(jsonPath("$[0].alarmType").value(memberAlarm.getType().name()));
    }

    private Member createMemberWithOauthId(final String oauthId) {
        final Member member = Member.withoutId(Oauth2RegistrationId.APPLE, oauthId, new MemberNickname("nickname"));
        return memberRepository.save(member);
    }

    private Battle createBattle() {
        return battleRepository.save(BattleFixture.initialBattleBuilder().status(BattleStatus.PROGRESS).build());
    }

    private BattleParticipant join(final Battle battle, final Member member) {
        return battleParticipantRepository.save(BattleParticipant.normalPlayer(battle.getId(), member.getId()));
    }
}
