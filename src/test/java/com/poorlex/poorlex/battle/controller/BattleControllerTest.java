package com.poorlex.poorlex.battle.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleRepository;
import com.poorlex.poorlex.battle.fixture.BattleCreateRequestFixture;
import com.poorlex.poorlex.battle.service.BattleService;
import com.poorlex.poorlex.battle.service.dto.request.BattleCreateRequest;
import com.poorlex.poorlex.battle.service.dto.request.BattleFindRequest;
import com.poorlex.poorlex.expenditure.domain.ExpenditureRepository;
import com.poorlex.poorlex.expenditure.fixture.ExpenditureFixture;
import com.poorlex.poorlex.member.domain.Member;
import com.poorlex.poorlex.member.domain.MemberNickname;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.participate.domain.BattleParticipant;
import com.poorlex.poorlex.participate.domain.BattleParticipantRepository;
import com.poorlex.poorlex.support.IntegrationTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.TestMemberTokenGenerator;
import com.poorlex.poorlex.token.JwtTokenProvider;
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

        //when
        //then
        mockMvc.perform(get("/battles"))
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
        mockMvc.perform(
                get("/battles/progress")
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
        mockMvc.perform(get("/battles/complete")
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

    @Test
    void Id에_해당하는_배틀을_조회시_상태코드_200_과_배틀의_데이터를_반환한다() throws Exception {
        //given
        final Member member = Member.withoutId("oauthId", new MemberNickname("nickname"));
        final Long battleId = createBattle();
        final Battle battle = battleRepository.findById(battleId).orElseThrow(IllegalArgumentException::new);

        join(member, battle);
        startBattle(battle);
        expend(1000, member, battle.getDuration().getStart());
        endBattle(battle);

        final BattleFindRequest request = new BattleFindRequest(LocalDate.now());

        //when
        //then
        mockMvc.perform(
                get("/battles/{battleId}", battleId)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.battleName").value(battle.getName()))
            .andExpect(jsonPath("$.maxParticipantSize").value(battle.getMaxParticipantSize().getValue()))
            .andExpect(jsonPath("$.currentParticipantSize").value(2))
            .andExpect(jsonPath("$.battleBudget").value(10000))
            .andExpect(jsonPath("$.battleDDay").value(battle.getDDay(request.getDate())))
            .andExpect(jsonPath("$.rankings.length()").value(2))
            .andExpect(jsonPath("$.rankings[0].rank").value(1))
            .andExpect(jsonPath("$.rankings[0].level").value(1))
            .andExpect(jsonPath("$.rankings[0].manager").value(true))
            .andExpect(jsonPath("$.rankings[0].expenditure").value(0))
            .andExpect(jsonPath("$.rankings[1].rank").value(2))
            .andExpect(jsonPath("$.rankings[1].level").value(1))
            .andExpect(jsonPath("$.rankings[1].manager").value(false))
            .andExpect(jsonPath("$.rankings[1].nickname").value(member.getNickname()))
            .andExpect(jsonPath("$.rankings[1].expenditure").value(1000));
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
