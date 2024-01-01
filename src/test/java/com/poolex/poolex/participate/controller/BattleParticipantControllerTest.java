package com.poolex.poolex.participate.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poolex.poolex.auth.domain.Member;
import com.poolex.poolex.auth.domain.MemberNickname;
import com.poolex.poolex.auth.domain.MemberRepository;
import com.poolex.poolex.battle.fixture.BattleCreateRequestFixture;
import com.poolex.poolex.battle.service.BattleService;
import com.poolex.poolex.support.IntegrationTest;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import com.poolex.poolex.token.JwtTokenProvider;
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

    @Test
    void 배틀참가자를_추가한다() throws Exception {
        //given
        final Member member = createMember();
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

    private Member createMember() {
        final Member member = Member.withoutId("oauthId", new MemberNickname("nickname"));
        return memberRepository.save(member);
    }

    private Long createBattle(final Long memberId) {
        return battleService.create(memberId, BattleCreateRequestFixture.simple());
    }
}
