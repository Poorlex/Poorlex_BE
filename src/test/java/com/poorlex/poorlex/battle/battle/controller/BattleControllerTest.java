package com.poorlex.poorlex.battle.battle.controller;

import com.poorlex.poorlex.auth.service.JwtTokenProvider;
import com.poorlex.poorlex.battle.battle.domain.Battle;
import com.poorlex.poorlex.battle.battle.domain.BattleRepository;
import com.poorlex.poorlex.battle.battle.service.BattleImageService;
import com.poorlex.poorlex.battle.battle.service.BattleService;
import com.poorlex.poorlex.battle.battle.service.dto.request.BattleCreateRequest;
import com.poorlex.poorlex.battle.battle.service.dto.request.BattleFindRequest;
import com.poorlex.poorlex.battle.battle.service.event.BattleCreatedEvent;
import com.poorlex.poorlex.battle.participation.domain.BattleParticipant;
import com.poorlex.poorlex.battle.participation.domain.BattleParticipantRepository;
import com.poorlex.poorlex.battle.participation.service.BattleParticipantEventHandler;
import com.poorlex.poorlex.consumption.expenditure.domain.ExpenditureRepository;
import com.poorlex.poorlex.consumption.expenditure.fixture.ExpenditureFixture;
import com.poorlex.poorlex.support.IntegrationTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.TestMemberTokenGenerator;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberNickname;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @MockBean
    private BattleImageService imageService;

    @MockBean
    private BattleParticipantEventHandler battleParticipantEventHandler;

    private TestMemberTokenGenerator testMemberTokenGenerator;

    @BeforeEach
    void setUp() {
        this.testMemberTokenGenerator = new TestMemberTokenGenerator(memberRepository, jwtTokenProvider);
        doNothing().when(battleParticipantEventHandler).handle(any(BattleCreatedEvent.class));
        given(imageService.saveAndReturnPath(any(), any())).willReturn("imageUrl");
    }

    @Test
    void 배틀을_성공적으로_생성시_상태코드_201을_반환한다() throws Exception {
        //given
        final String accessToken = testMemberTokenGenerator.createTokenWithNewMember("nickname");
        final MockMultipartFile image = new MockMultipartFile("image",
                                                              "cat-8415620_640",
                                                              MediaType.MULTIPART_FORM_DATA_VALUE,
                                                              new FileInputStream(
                                                                      "src/test/resources/testImage/cat-8415620_640.jpg"));

        //when
        //then
        mockMvc.perform(
                        multipart(HttpMethod.POST, "/battles")
                                .file(image)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                                .queryParam("name", "배틀 이름")
                                .queryParam("introduction", "배틀 소개")
                                .queryParam("budget", "10000")
                                .queryParam("maxParticipantSize", "10")
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION));
    }

    @Test
    void 배틀들을_조회시_상태코드_200_과_배틀의_데이터를_반환한다() throws Exception {
        //given
        Member manager = createMember();
        final Long battleId = createBattle(manager);
        joinNewNormalPlayerWithOauthId("oauthId", battleId);

        //when
        //then
        mockMvc.perform(get("/battles"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].battleId").value(battleId))
                .andExpect(jsonPath("$[0].name").value("배틀 이름"))
                .andExpect(jsonPath("$[0].imageUrl").value("imageUrl"))
                .andExpect(jsonPath("$[0].difficulty").value("HARD"))
                .andExpect(jsonPath("$[0].budget").value(10000))
                .andExpect(jsonPath("$[0].currentParticipant").value(2))
                .andExpect(jsonPath("$[0].maxParticipantCount").value(10));
    }

    @Test
    void 현재_진행중인_배틀들을_조회시_상태코드_200_과_배틀의_데이터를_반환한다() throws Exception {
        //given
        final Member member = Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname"));
        final Member manager = createMember();
        final Long battleId = createBattle(manager);
        final Battle battle = battleRepository.findById(battleId).orElseThrow(IllegalArgumentException::new);

        join(member, battle);
        startBattle(battle);
        expend(1000L, member, LocalDate.from(battle.getDuration().getStart()));

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
                .andExpect(jsonPath("$[0].name").value("배틀 이름"))
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
        final Member member = Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname"));
        final Member manager = createMember();
        final Long battleId = createBattle(manager);
        final Battle battle = battleRepository.findById(battleId).orElseThrow(IllegalArgumentException::new);

        join(member, battle);
        startBattle(battle);

        expend(1000L, member, LocalDate.from(battle.getDuration().getStart()));
        expend(1000L, member, LocalDate.from(battle.getDuration().getStart()).plusDays(1));
        expend(1000L, member, LocalDate.from(battle.getDuration().getStart()).plusDays(2));
        expend(1000L, member, LocalDate.from(battle.getDuration().getStart()).plusDays(3));
        expend(1000L, member, LocalDate.from(battle.getDuration().getStart()).plusDays(4));
        expend(1000L, member, LocalDate.from(battle.getDuration().getStart()).plusDays(5));
        expend(1000L, member, LocalDate.from(battle.getDuration().getStart()).plusDays(6));

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
                .andExpect(jsonPath("$[0].name").value("배틀 이름"))
                .andExpect(jsonPath("$[0].imageUrl").value("imageUrl"))
                .andExpect(jsonPath("$[0].difficulty").value("HARD"))
                .andExpect(jsonPath("$[0].pastDay").value(battle.getNumberOfDayPassedAfterEnd(LocalDate.now())))
                .andExpect(jsonPath("$[0].budgetLeft").value(10000 - (1000 * 7)))
                .andExpect(jsonPath("$[0].earnedPoint").value(30))
                .andExpect(jsonPath("$[0].currentParticipantRank").value(1))
                .andExpect(jsonPath("$[0].battleParticipantCount").value(2));
    }

    @Test
    void Id에_해당하는_배틀을_조회시_상태코드_200_과_배틀의_데이터를_반환한다() throws Exception {
        //given
        final Member member = Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname"));
        final Member manager = createMember();
        final Long battleId = createBattle(manager);
        final Battle battle = battleRepository.findById(battleId).orElseThrow(IllegalArgumentException::new);

        join(member, battle);
        startBattle(battle);
        expend(1000L, member, LocalDate.from(battle.getDuration().getStart()));
        endBattle(battle);

        final BattleFindRequest request = new BattleFindRequest(LocalDate.now(), List.of());

        final String accessToken = testMemberTokenGenerator.createAccessToken(member);

        //when
        //then
        mockMvc.perform(
                        get("/battles/{battleId}", battleId)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(battle.getId()))
                .andExpect(jsonPath("$.battleName").value(battle.getName()))
                .andExpect(jsonPath("$.maxParticipantSize").value(battle.getMaxParticipantSize().getValue()))
                .andExpect(jsonPath("$.currentParticipantSize").value(2))
                .andExpect(jsonPath("$.battleBudget").value(battle.getBudget()))
                .andExpect(jsonPath("$.battleDDay").value(battle.getDDay(request.getDate())))
                .andExpect(jsonPath("$.battleIntroduction").value(battle.getIntroduction()))
                .andExpect(jsonPath("$.isParticipating").value(true))
                .andExpect(jsonPath("$.battleManager.nickname").value(manager.getNickname()))
                .andExpect(jsonPath("$.battleManager.level").isNumber())
                .andExpect(jsonPath("$.battleManager.description").doesNotExist());
    }

    @Test
    void ERROR_배틀생성시_이름이_공백으로만_이루어져_있으면_상태코드_400을_반환한다() throws Exception {
        //given
        final MockMultipartFile image = new MockMultipartFile(
                "image",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );
        final String accessToken = testMemberTokenGenerator.createTokenWithNewMember("nickname");

        //when
        //then
        mockMvc.perform(
                        multipart(HttpMethod.POST, "/battles")
                                .file(image)
                                .queryParam("name", "    ")
                                .queryParam("introduction", "배틀 소개")
                                .queryParam("budget", "10000")
                                .queryParam("maxParticipantSize", "10")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void ERROR_배틀생성시_소개가_공백으로만_이루어져_있으면_상태코드_400을_반환한다() throws Exception {
        //given
        final MockMultipartFile image = new MockMultipartFile(
                "image",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );
        final String accessToken = testMemberTokenGenerator.createTokenWithNewMember("nickname");

        //when
        //then
        mockMvc.perform(
                        multipart(HttpMethod.POST, "/battles")
                                .file(image)
                                .queryParam("name", "배틀 이름")
                                .queryParam("introduction", "    ")
                                .queryParam("budget", "10000")
                                .queryParam("maxParticipantSize", "10")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void ERROR_배틀생성시_예산이_만원미만이면_상태코드_400을_반환한다() throws Exception {
        //given
        final MockMultipartFile image = new MockMultipartFile(
                "image",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );
        final String accessToken = testMemberTokenGenerator.createTokenWithNewMember("nickname");

        //when
        //then
        mockMvc.perform(
                        multipart(HttpMethod.POST, "/battles")
                                .file(image)
                                .queryParam("name", "배틀 이름")
                                .queryParam("introduction", "배틀 소개")
                                .queryParam("budget", "0")
                                .queryParam("maxParticipantSize", "10")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void ERROR_배틀생성시_예산이_20만원초과이면_상태코드_400을_반환한다() throws Exception {
        //given
        final MockMultipartFile image = new MockMultipartFile(
                "image",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );
        final String accessToken = testMemberTokenGenerator.createTokenWithNewMember("nickname");

        //when
        //then
        mockMvc.perform(
                        multipart(HttpMethod.POST, "/battles")
                                .file(image)
                                .queryParam("name", "배틀 이름")
                                .queryParam("introduction", "배틀 소개")
                                .queryParam("budget", "210000")
                                .queryParam("maxParticipantSize", "10")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void ERROR_배틀생성시_예산이_만원단위가_아니면_상태코드_400을_반환한다() throws Exception {
        //given
        final MockMultipartFile image = new MockMultipartFile(
                "image",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );
        final String accessToken = testMemberTokenGenerator.createTokenWithNewMember("nickname");

        //when
        //then
        mockMvc.perform(
                        multipart(HttpMethod.POST, "/battles")
                                .file(image)
                                .queryParam("name", "배틀 이름")
                                .queryParam("introduction", "배틀 소개")
                                .queryParam("budget", "9000")
                                .queryParam("maxParticipantSize", "10")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void ERROR_배틀생성시_최대인원이_1미만이면_상태코드_400을_반환한다() throws Exception {
        //given
        final MockMultipartFile image = new MockMultipartFile(
                "image",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );
        final String accessToken = testMemberTokenGenerator.createTokenWithNewMember("nickname");

        //when
        //then
        mockMvc.perform(
                        multipart(HttpMethod.POST, "/battles")
                                .file(image)
                                .queryParam("name", "배틀 이름")
                                .queryParam("introduction", "배틀 소개")
                                .queryParam("budget", "10000")
                                .queryParam("maxParticipantSize", "0")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void ERROR_배틀생성시_최대인원이_10초과이면_상태코드_400을_반환한다() throws Exception {
        //given
        final MockMultipartFile image = new MockMultipartFile(
                "image",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );
        final String accessToken = testMemberTokenGenerator.createTokenWithNewMember("nickname");

        //when
        //then
        mockMvc.perform(
                        multipart(HttpMethod.POST, "/battles")
                                .file(image)
                                .queryParam("name", "배틀 이름")
                                .queryParam("introduction", "배틀 소개")
                                .queryParam("budget", "10000")
                                .queryParam("maxParticipantSize", "11")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    private Member createMember() {
        final Member member = Member.withoutId(Oauth2RegistrationId.APPLE,
                UUID.randomUUID().toString(),
                new MemberNickname("nickname"));
        return memberRepository.save(member);
    }

    private Long createBattle(Member manager) throws Exception {
        final MockMultipartFile image = new MockMultipartFile(
                "image",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );
        Long battleId = battleService.create(manager.getId(), image, new BattleCreateRequest("배틀 이름", "배틀 소개", 10000, 10));

        BattleParticipant battleParticipant = BattleParticipant.manager(battleId, manager.getId());
        battleParticipantRepository.save(battleParticipant);

        return battleId;
    }

    private void joinNewNormalPlayerWithOauthId(final String oauthId, final Long battleId) {
        final Member member = Member.withoutId(Oauth2RegistrationId.APPLE, oauthId, new MemberNickname("nickname"));
        memberRepository.save(member);

        final BattleParticipant battleParticipant = BattleParticipant.normalPlayer(battleId, member.getId());
        battleParticipantRepository.save(battleParticipant);
    }

    private void join(final Member member, final Battle battle) {
        memberRepository.save(member);
        final BattleParticipant battleParticipant = BattleParticipant.normalPlayer(battle.getId(), member.getId());
        battleParticipantRepository.save(battleParticipant);
    }

    private void startBattle(final Battle battle) {
        battleService.startBattle(battle.getId(), battle.getDuration().getStart());
    }

    private void endBattle(final Battle battle) {
        battleService.endBattle(battle.getId(), battle.getDuration().getEnd());
    }

    private void expend(final Long amount, final Member member, final LocalDate date) {
        expenditureRepository.save(ExpenditureFixture.simpleWithMainImage(amount, member.getId(), date));
    }
}
