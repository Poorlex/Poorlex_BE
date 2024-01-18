package com.poolex.poolex.battlealarmreaction.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poolex.poolex.alarm.battlealarm.domain.BattleAlarm;
import com.poolex.poolex.alarm.battlealarm.domain.BattleAlarmRepository;
import com.poolex.poolex.alarm.battlealarm.domain.BattleAlarmType;
import com.poolex.poolex.auth.domain.Member;
import com.poolex.poolex.auth.domain.MemberNickname;
import com.poolex.poolex.auth.domain.MemberRepository;
import com.poolex.poolex.battle.domain.Battle;
import com.poolex.poolex.battle.domain.BattleRepository;
import com.poolex.poolex.battle.fixture.BattleFixture;
import com.poolex.poolex.battlealarmreaction.service.dto.request.BattleAlarmReactionCreateRequest;
import com.poolex.poolex.support.IntegrationTest;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import com.poolex.poolex.support.TestMemberTokenGenerator;
import com.poolex.poolex.token.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

class BattleBattleBattleAlarmReactionControllerTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @Autowired
    private BattleAlarmRepository battleAlarmRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private TestMemberTokenGenerator memberTokenGenerator;

    @BeforeEach
    void setUp() {
        this.memberTokenGenerator = new TestMemberTokenGenerator(memberRepository, jwtTokenProvider);
    }

    @Test
    void 칭찬_알림_반응을_생성한다() throws Exception {
        //given
        final Battle battle = createBattle();
        final Member member = createMember("oauthId");
        final BattleAlarm battleAlarm = createBattleAlarm(battle, member, BattleAlarmType.EXPENDITURE_CREATED);

        final String accessToken = memberTokenGenerator.createAccessToken(member);
        final BattleAlarmReactionCreateRequest request = new BattleAlarmReactionCreateRequest(battleAlarm.getId(),
            "PRAISE",
            "알림 반응 문구");

        //when
        //then
        mockMvc.perform(
                post("/battle-alarm-reaction")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isCreated());
    }

    @Test
    void 혼내기_알림_반응을_생성한다() throws Exception {
        //given
        final Battle battle = createBattle();
        final Member member = createMember("oauthId");
        final BattleAlarm battleAlarm = createBattleAlarm(battle, member, BattleAlarmType.EXPENDITURE_CREATED);

        final String accessToken = memberTokenGenerator.createAccessToken(member);
        final BattleAlarmReactionCreateRequest request = new BattleAlarmReactionCreateRequest(battleAlarm.getId(),
            "SCOLD",
            "알림 반응 문구");

        //when
        //then
        mockMvc.perform(
                post("/battle-alarm-reaction")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isCreated());
    }

    private Battle createBattle() {
        return battleRepository.save(BattleFixture.simple());
    }

    private Member createMember(final String oauthId) {
        return memberRepository.save(Member.withoutId(oauthId, new MemberNickname("nickname")));
    }

    private BattleAlarm createBattleAlarm(final Battle battle, final Member member, final BattleAlarmType type) {
        return battleAlarmRepository.save(BattleAlarm.withoutId(battle.getId(), member.getId(), type));
    }
}
