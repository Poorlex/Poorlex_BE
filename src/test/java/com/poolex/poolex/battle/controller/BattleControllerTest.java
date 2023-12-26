package com.poolex.poolex.battle.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poolex.poolex.battle.fixture.BattleCreateRequestFixture;
import com.poolex.poolex.battle.service.dto.request.BattleCreateRequest;
import com.poolex.poolex.login.domain.Member;
import com.poolex.poolex.login.domain.MemberNickname;
import com.poolex.poolex.login.domain.MemberRepository;
import com.poolex.poolex.participate.domain.BattleParticipant;
import com.poolex.poolex.participate.domain.BattleParticipantRepository;
import com.poolex.poolex.support.IntegrationTest;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import com.poolex.poolex.support.TestMemberTokenGenerator;
import com.poolex.poolex.token.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

@DisplayName("배틀 인수 테스트")
class BattleControllerTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BattleParticipantRepository battleParticipantRepository;

    private TestMemberTokenGenerator testMemberTokenGenerator;

    @BeforeEach
    void setUp() {
        this.testMemberTokenGenerator = new TestMemberTokenGenerator(memberRepository, jwtTokenProvider);
    }

    @Test
    void 배틀을_성공적으로_생성시_상태코드_201을_반환한다() throws Exception {
        //given
        final String accessToken = testMemberTokenGenerator.createTokenWithNewMember("nickname");
        final BattleCreateRequest request = BattleCreateRequestFixture.simple();

        //when
        //then
        mockMvc.perform(
                post("/battles")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(header().exists(HttpHeaders.LOCATION));
    }

    @Test
    void 현재_참여가능한_배틀들을_조회시_상태코드_200_과_배틀의_데이터를_반환한다() throws Exception {
        //given
        final Long battleId = createBattle();
        addNormalPlayer(battleId);
        final String accessToken = testMemberTokenGenerator.createTokenWithNewMember("nickname");

        //when
        mockMvc.perform(get("/battles")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].name").value("name"))
            .andExpect(jsonPath("$[0].imageUrl").value("imageUrl"))
            .andExpect(jsonPath("$[0].difficulty").value("HARD"))
            .andExpect(jsonPath("$[0].dday").exists())
            .andExpect(jsonPath("$[0].currentParticipant").value(2))
            .andExpect(jsonPath("$[0].maxParticipantCount").value(10));
    }

    private Long createBattle() throws Exception {
        //given
        final String accessToken = testMemberTokenGenerator.createTokenWithNewMember("nickname");
        final BattleCreateRequest request = BattleCreateRequestFixture.simple();

        //when
        final MvcResult mvcResult = mockMvc.perform(
                post("/battles")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isCreated())
            .andReturn();

        final String locationHeader = mvcResult.getResponse().getHeader(HttpHeaders.LOCATION);
        return Long.parseLong(locationHeader.substring(locationHeader.lastIndexOf('/') + 1));
    }

    private void addNormalPlayer(final Long battleId) {
        final Member member = memberRepository.save(Member.withoutId(new MemberNickname("nickname")));

        final BattleParticipant battleParticipant = BattleParticipant.normalPlayer(
            battleId,
            member.getId()
        );

        battleParticipantRepository.save(battleParticipant);
    }
}
