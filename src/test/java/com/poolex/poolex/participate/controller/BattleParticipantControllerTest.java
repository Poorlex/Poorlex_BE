package com.poolex.poolex.participate.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poolex.poolex.battle.fixture.BattleCreateRequestFixture;
import com.poolex.poolex.battle.service.BattleService;
import com.poolex.poolex.member.domain.Member;
import com.poolex.poolex.member.domain.MemberNickname;
import com.poolex.poolex.member.domain.MemberRepository;
import com.poolex.poolex.participate.domain.BattleParticipant;
import com.poolex.poolex.participate.domain.BattleParticipantRepository;
import com.poolex.poolex.support.IntegrationTest;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import com.poolex.poolex.token.JwtTokenProvider;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

@DisplayName("배틀 참가 인수 테스트")
class BattleParticipantControllerTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BattleService battleService;

    @Autowired
    private BattleParticipantRepository battleParticipantRepository;

    @Test
    void 배틀참가자를_추가한다() throws Exception {
        //given
        final Member member = createMember("oauthId");
        final Long battleId = createBattle(member.getId());
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        //when
        //then
        mockMvc.perform(
                post("/battles/{battleId}/participants", battleId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            )
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(header().exists(HttpHeaders.LOCATION));
    }

    @Test
    void 배틀참가자를_제거한다() throws Exception {
        //given
        final Member member1 = createMember("oauthId1");
        final Member member2 = createMember("oauthId2");
        final Long battleId = createBattle(member1.getId());
        join(battleId, member2);
        final String accessToken = jwtTokenProvider.createAccessToken(member2.getId());

        //when
        //then
        mockMvc.perform(
                delete("/battles/{battleId}/participants", battleId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            )
            .andDo(print())
            .andExpect(status().isNoContent());

        final Optional<BattleParticipant> removedBattleParticipant = battleParticipantRepository.findByBattleIdAndMemberId(
            battleId,
            member2.getId()
        );
        assertThat(removedBattleParticipant).isEmpty();
    }

    private Member createMember(final String oauthId) {
        final Member member = Member.withoutId(oauthId, new MemberNickname("nickname"));
        return memberRepository.save(member);
    }

    private Long createBattle(final Long memberId) {
        return battleService.create(memberId, BattleCreateRequestFixture.simple());
    }

    private void join(final Long battleId, final Member member) {
        battleParticipantRepository.save(BattleParticipant.normalPlayer(battleId, member.getId()));
    }
}
