package com.poorlex.poorlex.battle.participation.controller;

import com.poorlex.poorlex.battle.battle.domain.Battle;
import com.poorlex.poorlex.battle.battle.domain.BattleRepository;
import com.poorlex.poorlex.battle.battle.domain.BattleStatus;
import com.poorlex.poorlex.battle.battle.fixture.BattleFixture;
import com.poorlex.poorlex.battle.participation.domain.BattleParticipant;
import com.poorlex.poorlex.battle.participation.domain.BattleParticipantRepository;
import com.poorlex.poorlex.support.IntegrationTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.auth.service.JwtTokenProvider;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberNickname;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("배틀 참가 인수 테스트")
class BattleParticipantControllerTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private BattleParticipantRepository battleParticipantRepository;

    @Test
    void 배틀참가자를_추가한다() throws Exception {
        //given
        final Member member = createMember("oauthId");
        final Battle battle = createBattle();
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        //when
        //then
        mockMvc.perform(
                        post("/battles/{battleId}/participants", battle.getId())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION));
    }

    @Test
    void ERROR_배틀참가자가_이미_3개의_배틀에_참가중인경우_400_상태코드로_응답한다() throws Exception {
        //given
        final Member member = createMember("oauthId");
        join(createBattle().getId(), member);
        join(createBattle().getId(), member);
        join(createBattle().getId(), member);

        final Battle battle = createBattle();
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        //when
        //then
        mockMvc.perform(
                        post("/battles/{battleId}/participants", battle.getId())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @ParameterizedTest
    @CsvSource(value = {"RECRUITING_FINISHED", "PROGRESS", "COMPLETE"})
    void ERROR_배틀참가자가_참가하려는_빼틀이_모집중이_아닌_경우_400_상태코드로_응답한다(final BattleStatus battleStatus) throws Exception {
        //given
        final Member member = createMember("oauthId");
        final Battle battle = createBattleWithStatus(battleStatus);
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        //when
        //then
        mockMvc.perform(
                        post("/battles/{battleId}/participants", battle.getId())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void 배틀참가자를_제거한다() throws Exception {
        //given
        final Member member1 = createMember("oauthId1");
        final Member member2 = createMember("oauthId2");
        final Battle battle = createBattle();
        join(battle.getId(), member2);
        final String accessToken = jwtTokenProvider.createAccessToken(member2.getId());

        //when
        //then
        mockMvc.perform(
                        delete("/battles/{battleId}/participants", battle.getId())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andDo(print())
                .andExpect(status().isNoContent());

        final Optional<BattleParticipant> removedBattleParticipant = battleParticipantRepository.findByBattleIdAndMemberId(
                battle.getId(),
                member2.getId()
        );
        assertThat(removedBattleParticipant).isEmpty();
    }

    @Test
    void ERROR_배틀참가자가_탈퇴하려는_빼틀이_종료된_아닌_경우_400_상태코드로_응답한다() throws Exception {
        //given
        final Member member = createMember("oauthId");
        final Battle battle = createBattleWithStatus(BattleStatus.COMPLETE);
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        //when
        //then
        mockMvc.perform(
                        delete("/battles/{battleId}/participants", battle.getId())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    private Member createMember(final String oauthId) {
        final Member member = Member.withoutId(Oauth2RegistrationId.APPLE, oauthId, new MemberNickname("nickname"));
        return memberRepository.save(member);
    }

    private Battle createBattle() {
        return battleRepository.save(BattleFixture.simple());
    }

    private Battle createBattleWithStatus(final BattleStatus battleStatus) {
        return battleRepository.save(
                BattleFixture.initialBattleBuilder()
                        .status(battleStatus)
                        .build()
        );
    }

    private void join(final Long battleId, final Member member) {
        battleParticipantRepository.save(BattleParticipant.normalPlayer(battleId, member.getId()));
    }
}
