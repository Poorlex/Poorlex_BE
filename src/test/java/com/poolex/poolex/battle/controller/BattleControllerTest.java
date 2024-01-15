package com.poolex.poolex.battle.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poolex.poolex.auth.domain.Member;
import com.poolex.poolex.auth.domain.MemberNickname;
import com.poolex.poolex.auth.domain.MemberRepository;
import com.poolex.poolex.battle.domain.Battle;
import com.poolex.poolex.battle.domain.BattleRepository;
import com.poolex.poolex.battle.fixture.BattleCreateRequestFixture;
import com.poolex.poolex.battle.service.BattleService;
import com.poolex.poolex.battle.service.dto.request.BattleCreateRequest;
import com.poolex.poolex.expenditure.domain.ExpenditureRepository;
import com.poolex.poolex.expenditure.fixture.ExpenditureFixture;
import com.poolex.poolex.participate.domain.BattleParticipant;
import com.poolex.poolex.participate.domain.BattleParticipantRepository;
import com.poolex.poolex.support.IntegrationTest;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import com.poolex.poolex.support.TestMemberTokenGenerator;
import com.poolex.poolex.token.JwtTokenProvider;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private ExpenditureRepository expenditureRepository;

    @Autowired
    private BattleService battleService;

    private TestMemberTokenGenerator testMemberTokenGenerator;

    @BeforeEach
    void setUp() {
        this.testMemberTokenGenerator = new TestMemberTokenGenerator(memberRepository, jwtTokenProvider);
    }

    @Test
    void 배틀을_성공적으로_생성시_상태코드_201을_반환한다() throws Exception {
        //given
        final BattleCreateRequest request = BattleCreateRequestFixture.simple();
        final String accessToken = testMemberTokenGenerator.createTokenWithNewMember("nickname");

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
        joinNewNormalPlayerWithOauthId("oauthId", battleId);

        final String accessToken = testMemberTokenGenerator.createTokenWithNewMember("oauthId2");

        //when
        //then
        mockMvc.perform(get("/battles")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].battleId").value(battleId))
            .andExpect(jsonPath("$[0].name").value("name"))
            .andExpect(jsonPath("$[0].imageUrl").value("imageUrl"))
            .andExpect(jsonPath("$[0].difficulty").value("HARD"))
            .andExpect(jsonPath("$[0].budget").value(10000))
            .andExpect(jsonPath("$[0].currentParticipant").value(2))
            .andExpect(jsonPath("$[0].maxParticipantCount").value(10));
    }

    @Test
    void 현재_진행중인_배틀들을_조회시_상태코드_200_과_배틀의_데이터를_반환한다() throws Exception {
        //given
        final Member member = Member.withoutId("oauthId", new MemberNickname("nickname"));
        final Long battleId = createBattle();
        final Battle battle = battleRepository.findById(battleId).orElseThrow(IllegalArgumentException::new);

        join(member, battle);
        startBattle(battle);
        expend(1000, member, battle.getDuration().getStart());

        final String accessToken = testMemberTokenGenerator.createAccessToken(member);

        //when
        //then
        mockMvc.perform(get("/battles?status=progress")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].battleId").value(battleId))
            .andExpect(jsonPath("$[0].name").value("name"))
            .andExpect(jsonPath("$[0].imageUrl").value("imageUrl"))
            .andExpect(jsonPath("$[0].difficulty").value("HARD"))
            .andExpect(jsonPath("$[0].dday").value(battle.getDDay(LocalDate.now())))
            .andExpect(jsonPath("$[0].budgetLeft").value(10000 - 1000))
            .andExpect(jsonPath("$[0].currentParticipantRank").value(2))
            .andExpect(jsonPath("$[0].battleParticipantCount").value(2))
            .andExpect(jsonPath("$[0].uncheckedAlarmCount").value(0));
    }

    @Test
    void 완료된_배틀들을_조회시_상태코드_200_과_배틀의_데이터를_반환한다() throws Exception {
        //given
        final Member member = Member.withoutId("oauthId", new MemberNickname("nickname"));
        final Long battleId = createBattle();
        final Battle battle = battleRepository.findById(battleId).orElseThrow(IllegalArgumentException::new);

        join(member, battle);
        startBattle(battle);
        expend(1000, member, battle.getDuration().getStart());
        endBattle(battle);

        final String accessToken = testMemberTokenGenerator.createAccessToken(member);

        //when
        //then
        mockMvc.perform(get("/battles?status=complete")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].battleId").value(battleId))
            .andExpect(jsonPath("$[0].name").value("name"))
            .andExpect(jsonPath("$[0].imageUrl").value("imageUrl"))
            .andExpect(jsonPath("$[0].difficulty").value("HARD"))
            .andExpect(jsonPath("$[0].pastDay").value(battle.getPastDay(LocalDate.now())))
            .andExpect(jsonPath("$[0].budgetLeft").value(10000 - 1000))
            .andExpect(jsonPath("$[0].earnedPoint").value(20))
            .andExpect(jsonPath("$[0].currentParticipantRank").value(2))
            .andExpect(jsonPath("$[0].battleParticipantCount").value(2));
    }

    private Long createBattle() throws Exception {
        final String accessToken = testMemberTokenGenerator.createTokenWithNewMember("nickname");

        final MvcResult mvcResult = mockMvc.perform(
            post("/battles")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .content(objectMapper.writeValueAsString(BattleCreateRequestFixture.simple()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn();

        final String locationHeader = mvcResult.getResponse().getHeader(HttpHeaders.LOCATION);
        return Long.parseLong(
            locationHeader.substring(locationHeader.lastIndexOf('/') + 1)
        );
    }

    private Member joinNewNormalPlayerWithOauthId(final String oauthId, final Long battleId) {
        final Member member = Member.withoutId(oauthId, new MemberNickname("nickname"));
        memberRepository.save(member);

        final BattleParticipant battleParticipant = BattleParticipant.normalPlayer(battleId, member.getId());
        battleParticipantRepository.save(battleParticipant);

        return member;
    }

    private Member join(final Member member, final Battle battle) {
        memberRepository.save(member);
        final BattleParticipant battleParticipant = BattleParticipant.normalPlayer(battle.getId(), member.getId());
        battleParticipantRepository.save(battleParticipant);

        return member;
    }

    private void startBattle(final Battle battle) {
        battleService.startBattle(battle.getId(), battle.getDuration().getStart());
    }

    private void endBattle(final Battle battle) {
        battleService.endBattle(battle.getId(), battle.getDuration().getEnd());
    }

    private void expend(final int amount, final Member member, final LocalDateTime date) {
        expenditureRepository.save(ExpenditureFixture.simpleWith(amount, member.getId(), date));
    }
}
