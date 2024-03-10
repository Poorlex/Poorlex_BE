package com.poorlex.poorlex.alarm.battlealarm.controller;

import com.poorlex.poorlex.alarm.battlealarm.domain.*;
import com.poorlex.poorlex.alarm.battlealarm.service.dto.request.BattleAlarmRequest;
import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleRepository;
import com.poorlex.poorlex.battle.domain.BattleStatus;
import com.poorlex.poorlex.battle.fixture.BattleFixture;
import com.poorlex.poorlex.member.domain.Member;
import com.poorlex.poorlex.member.domain.MemberNickname;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.member.domain.Oauth2RegistrationId;
import com.poorlex.poorlex.participate.domain.BattleParticipant;
import com.poorlex.poorlex.participate.domain.BattleParticipantRepository;
import com.poorlex.poorlex.support.IntegrationTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.token.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RecordApplicationEvents
class BattleAlarmControllerTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private BattleAlarmRepository battleAlarmRepository;

    @Autowired
    private BattleParticipantRepository battleParticipantRepository;

    @Autowired
    private BattleAlarmViewHistoryRepository battleAlarmViewHistoryRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MemberRepository memberRepository;


    @Test
    void 배틀의_알림목록을_조회하고_조회내역을_저장한다_알림이_있을_때() throws Exception {
        //given
        final Battle battle = createBattle();
        final Member member = createMemberWithOauthId("oauthId");
        join(member, battle);
        final BattleAlarm battleAlarm = createAlarm(battle, member.getId());
        final BattleAlarmRequest request = new BattleAlarmRequest(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        //when
        mockMvc.perform(
                get("/battles/{battleId}/alarms", battle.getId())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].alarmId").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].memberId").value(member.getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].alarmType").value(battleAlarm.getType().name()))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].createdAt").isNotEmpty());

        //then
        assertSoftly(
            softly -> {
                final Optional<BattleAlarmViewHistory> viewHistory =
                    battleAlarmViewHistoryRepository.findByBattleIdAndMemberId(battle.getId(), member.getId());

                softly.assertThat(viewHistory)
                    .isPresent()
                    .get()
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(BattleAlarmViewHistory.withoutId(battle.getId(), member.getId(), request.getDateTime()));
            }
        );
    }

    @Test
    void 배틀의_알림목록을_조회하고_조회내역을_저장한다_알림이_없을_때() throws Exception {
        //given
        final Battle battle = createBattle();
        final Member member = createMemberWithOauthId("oauthId");
        join(member, battle);
        final BattleAlarmRequest request = new BattleAlarmRequest(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        //when
        mockMvc.perform(
                get("/battles/{battleId}/alarms", battle.getId())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));

        //then
        assertSoftly(
            softly -> {
                final Optional<BattleAlarmViewHistory> viewHistory =
                    battleAlarmViewHistoryRepository.findByBattleIdAndMemberId(battle.getId(), member.getId());

                softly.assertThat(viewHistory)
                    .isPresent()
                    .get()
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(BattleAlarmViewHistory.withoutId(battle.getId(), member.getId(), request.getDateTime()));
            }
        );
    }

    @Test
    void 회원이_읽지않은_배틀알림의_개수를_조회한다_알림이_있을_때() throws Exception {
        //given
        final Battle battle = createBattle();
        final Member member = createMemberWithOauthId("oauthId");
        join(member, battle);
        createAlarm(battle, member.getId());
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        //when
        //then
        mockMvc.perform(
                get("/battles/{battleId}/alarms/unchecked", battle.getId())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            )
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.count").value(1));
    }

    @Test
    void 회원이_읽지않은_배틀알림의_개수를_조회한다_알림이_없을_떄() throws Exception {
        //given
        final Battle battle = createBattle();
        final Member member = createMemberWithOauthId("oauthId");
        join(member, battle);
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());
        //when
        //then
        mockMvc.perform(
                get("/battles/{battleId}/alarms/unchecked", battle.getId())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            )
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.count").value(0));
    }

    private Battle createBattle() {
        return battleRepository.save(BattleFixture.initialBattleBuilder().status(BattleStatus.PROGRESS).build());
    }

    private Member createMemberWithOauthId(final String oauthId) {
        final Member member = Member.withoutId(Oauth2RegistrationId.APPLE, oauthId, new MemberNickname("nickname"));
        return memberRepository.save(member);
    }

    private void join(final Member member, final Battle battle) {
        final BattleParticipant battleParticipant = BattleParticipant.normalPlayer(battle.getId(), member.getId());
        battleParticipantRepository.save(battleParticipant);
    }

    private BattleAlarm createAlarm(final Battle battle, final Long memberId) {
        return battleAlarmRepository.save(
            BattleAlarm.withoutId(battle.getId(), memberId, BattleAlarmType.EXPENDITURE_CREATED));
    }
}
