package com.poorlex.poorlex.expenditure.controller;

import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleRepository;
import com.poorlex.poorlex.battle.domain.BattleStatus;
import com.poorlex.poorlex.battle.fixture.BattleFixture;
import com.poorlex.poorlex.config.aws.AWSS3Service;
import com.poorlex.poorlex.expenditure.domain.Expenditure;
import com.poorlex.poorlex.expenditure.domain.ExpenditureRepository;
import com.poorlex.poorlex.expenditure.domain.WeeklyExpenditureDuration;
import com.poorlex.poorlex.expenditure.fixture.ExpenditureFixture;
import com.poorlex.poorlex.expenditure.service.dto.request.MemberWeeklyTotalExpenditureRequest;
import com.poorlex.poorlex.participate.domain.BattleParticipant;
import com.poorlex.poorlex.participate.domain.BattleParticipantRepository;
import com.poorlex.poorlex.support.IntegrationTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.TestMemberTokenGenerator;
import com.poorlex.poorlex.token.JwtTokenProvider;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberNickname;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId;
import java.io.FileInputStream;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("지출 인수 테스트")
class ExpenditureControllerTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private BattleParticipantRepository battleParticipantRepository;

    @Autowired
    private ExpenditureRepository expenditureRepository;

    @MockBean
    private AWSS3Service awss3Service;

    private TestMemberTokenGenerator testMemberTokenGenerator;

    @BeforeEach
    void setUp() {
        this.testMemberTokenGenerator = new TestMemberTokenGenerator(memberRepository, jwtTokenProvider);
    }

    @Test
    void 지출을_생성한다_이미지가_1개_일떄() throws Exception {
        //given
        given(awss3Service.uploadMultipartFile(any(), any())).willReturn("s3-image-url");
        final String accessToken = testMemberTokenGenerator.createTokenWithNewMember("oauthId");

        final MockMultipartFile mainImage = new MockMultipartFile(
                "mainImage",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );

        //when
        //then
        mockMvc.perform(
                        multipart(HttpMethod.POST, "/expenditures")
                                .file(mainImage)
                                .queryParam("amount", "1000")
                                .queryParam("description", "소개")
                                .queryParam("date", LocalDate.now().toString())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION));
    }

    @Test
    void 지출을_생성한다_이미지가_2개일_땨() throws Exception {
        //given
        given(awss3Service.uploadMultipartFile(any(), any())).willReturn("s3-image-url");
        final String accessToken = testMemberTokenGenerator.createTokenWithNewMember("oauthId");

        final MockMultipartFile mainImage = new MockMultipartFile(
                "mainImage",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );

        final MockMultipartFile subImage = new MockMultipartFile(
                "subImage",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );

        //when
        //then
        mockMvc.perform(
                        multipart(HttpMethod.POST, "/expenditures")
                                .file(mainImage)
                                .file(subImage)
                                .queryParam("amount", "1000")
                                .queryParam("description", "소개")
                                .queryParam("date", LocalDate.now().toString())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION));
    }

    @Test
    void ERROR_지출생성시_금액이_0보다_작으면_400_상태코드로_응답한다() throws Exception {
        //given
        given(awss3Service.uploadMultipartFile(any(), any())).willReturn("s3-image-url");
        final String accessToken = testMemberTokenGenerator.createTokenWithNewMember("oauthId");

        final MockMultipartFile image = new MockMultipartFile(
                "mainImage",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );

        //when
        //then
        mockMvc.perform(
                        multipart(HttpMethod.POST, "/expenditures")
                                .file(image)
                                .queryParam("amount", "-1")
                                .queryParam("description", "소개")
                                .queryParam("date", LocalDate.now().toString())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void ERROR_지출생성시_금액이_9999999보다_크면_400_상태코드로_응답한다() throws Exception {
        //given
        given(awss3Service.uploadMultipartFile(any(), any())).willReturn("s3-image-url");
        final String accessToken = testMemberTokenGenerator.createTokenWithNewMember("oauthId");

        final MockMultipartFile image = new MockMultipartFile(
                "mainImage",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );

        //when
        //then
        mockMvc.perform(
                        multipart(HttpMethod.POST, "/expenditures")
                                .file(image)
                                .queryParam("amount", "10000000")
                                .queryParam("description", "소개")
                                .queryParam("date", LocalDate.now().toString())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void ERROR_지출생성시_설명이_비어있는_경우_400_상태코드로_응답한다() throws Exception {
        //given
        given(awss3Service.uploadMultipartFile(any(), any())).willReturn("s3-image-url");
        final String accessToken = testMemberTokenGenerator.createTokenWithNewMember("oauthId");

        final MockMultipartFile image = new MockMultipartFile(
                "mainImage",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );

        //when
        //then
        mockMvc.perform(
                        multipart(HttpMethod.POST, "/expenditures")
                                .file(image)
                                .queryParam("amount", "1000")
                                .queryParam("description", "  ")
                                .queryParam("date", LocalDate.now().toString())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void ERROR_지출생성시_설명이_30자를_넘는_경우_400_상태코드로_응답한다() throws Exception {
        //given
        given(awss3Service.uploadMultipartFile(any(), any())).willReturn("s3-image-url");
        final String accessToken = testMemberTokenGenerator.createTokenWithNewMember("oauthId");

        final MockMultipartFile image = new MockMultipartFile(
                "mainImage",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );

        //when
        //then
        mockMvc.perform(
                        multipart(HttpMethod.POST, "/expenditures")
                                .file(image)
                                .queryParam("amount", "1000")
                                .queryParam("description", "a".repeat(31))
                                .queryParam("date", LocalDate.now().toString())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void ERROR_지출생성시_메인이미지가_없는_경우_500_상태코드로_응답한다() throws Exception {
        //given
        given(awss3Service.uploadMultipartFile(any(), any())).willReturn("s3-image-url");
        final String accessToken = testMemberTokenGenerator.createTokenWithNewMember("oauthId");

        final MockMultipartFile subImage = new MockMultipartFile(
                "subImage",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );

        //when
        //then
        mockMvc.perform(
                        multipart(HttpMethod.POST, "/expenditures")
                                .file(subImage)
                                .queryParam("amount", "1000")
                                .queryParam("description", "소개")
                                .queryParam("date", LocalDate.now().toString())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void ERROR_지출생성시_이미지가_없는_경우_500_상태코드로_응답한다() throws Exception {
        //given
        given(awss3Service.uploadMultipartFile(any(), any())).willReturn("s3-image-url");
        final String accessToken = testMemberTokenGenerator.createTokenWithNewMember("oauthId");


        //when
        //then
        mockMvc.perform(
                        multipart(HttpMethod.POST, "/expenditures")
                                .queryParam("amount", "1000")
                                .queryParam("description", "소개")
                                .queryParam("date", LocalDate.now().toString())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void 지출을_수정한다() throws Exception {
        //given
        final Member member = createMember("oauthId");
        final Expenditure expenditure = createExpenditureWithMainImage(1000L, member.getId(), LocalDate.now());
        final String accessToken = testMemberTokenGenerator.createAccessToken(member);

        final MockMultipartFile mainImage = new MockMultipartFile(
                "mainImage",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );

        final MockMultipartFile subImage = new MockMultipartFile(
                "subImage",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );

        given(awss3Service.uploadMultipartFile(eq(mainImage), any())).willReturn("newMainImage");
        given(awss3Service.uploadMultipartFile(eq(subImage), any())).willReturn("newSubImage");

        //when
        //then
        mockMvc.perform(
                        multipart(HttpMethod.PUT, "/expenditures/" + expenditure.getId())
                                .file(mainImage)
                                .file(subImage)
                                .queryParam("amount", "2000")
                                .queryParam("description", "업데이트된 소개")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void 멤버의_기간중의_지출의_총합을_구한다_지출이_있을_때() throws Exception {
        //given
        final Member member = createMember("oauthId");
        final LocalDate date = LocalDate.now();
        final WeeklyExpenditureDuration weeklyExpenditureDuration = WeeklyExpenditureDuration.from(date);

        createExpenditureWithMainImage(1000L, member.getId(), LocalDate.from(weeklyExpenditureDuration.getStart()));
        createExpenditureWithMainImage(2000L, member.getId(), LocalDate.from(weeklyExpenditureDuration.getStart()));

        final String accessToken = testMemberTokenGenerator.createAccessToken(member);
        final MemberWeeklyTotalExpenditureRequest request = new MemberWeeklyTotalExpenditureRequest(date);

        //when
        //then
        mockMvc.perform(
                        get("/expenditures/weekly")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(3000));
    }

    @Test
    void 멤버의_기간중의_지출의_총합을_구한다_지출이_없을_때() throws Exception {
        //given
        final Member member = createMember("oauthId");
        final LocalDate date = LocalDate.now();

        createExpenditureWithMainImage(1000L, member.getId(), date);
        createExpenditureWithMainImage(2000L, member.getId(), date);

        final String accessToken = testMemberTokenGenerator.createAccessToken(member);
        final MemberWeeklyTotalExpenditureRequest request =
                new MemberWeeklyTotalExpenditureRequest(date.plusDays(7));

        //when
        //then
        mockMvc.perform(
                        get("/expenditures/weekly?withDate=true")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(0));
    }

    @Test
    void Id에_해당하는_지출을_조회한다() throws Exception {
        //given
        final Member member = createMember("oauthId");
        final Expenditure expenditure = createExpenditureWithMainImage(1000L, member.getId(), LocalDate.now());

        //when
        //then
        mockMvc.perform(get("/expenditures/{expenditureId}", expenditure.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expenditure.getId()))
                .andExpect(jsonPath("$.date").value(LocalDate.from(expenditure.getDate()).toString()))
                .andExpect(jsonPath("$.amount").value(1000L))
                .andExpect(jsonPath("$.description").value(expenditure.getDescription()))
                .andExpect(jsonPath("$.mainImageUrl").value(expenditure.getMainImageUrl()))
                .andExpect(jsonPath("$.subImageUrl").value(expenditure.getSubImageUrl().orElse(null)));
    }

    @Test
    void 멤버의_지출목록을_조회한다() throws Exception {
        //given
        final Member member = createMember("oauthId");
        final Expenditure expenditure = createExpenditureWithMainImage(1000L, member.getId(), LocalDate.now());
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        //when
        //then
        mockMvc.perform(
                        get("/expenditures")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(expenditure.getId()))
                .andExpect(jsonPath("$[0].date").value(LocalDate.from(expenditure.getDate()).toString()))
                .andExpect(jsonPath("$[0].amount").value(1000L))
                .andExpect(jsonPath("$[0].description").value(expenditure.getDescription()))
                .andExpect(jsonPath("$[0].mainImageUrl").value(expenditure.getMainImageUrl()))
                .andExpect(jsonPath("$[0].subImageUrl").value(expenditure.getSubImageUrl().orElse(null)));
    }

    @Test
    void 배틀의_요일별_지출목록을_조회한다() throws Exception {
        //given
        final Member member = createMember("oauthId1");
        final Member other = createMember("oauthId2");
        final Battle battle = createBattle();

        join(member, battle);
        join(other, battle);

        final LocalDate battleStart = LocalDate.from(battle.getDuration().getStart());
        final Expenditure memberExpenditure = createExpenditureWithMainImage(1000L, member.getId(), battleStart);
        createExpenditureWithMainImage(1000L, member.getId(), battleStart.minusWeeks(1));
        final Expenditure otherExpenditure = createExpenditureWithMainImage(1000L, other.getId(), battleStart);
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        //when
        //then
        mockMvc.perform(
                        get("/battles/{battleId}/expenditures?dayOfWeek={dayOfWeek}",
                            battle.getId(),
                            battleStart.getDayOfWeek().name()
                        ).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(memberExpenditure.getId()))
                .andExpect(jsonPath("$[0].imageUrl").value(memberExpenditure.getMainImageUrl()))
                .andExpect(jsonPath("$[0].imageCount").value(memberExpenditure.getImageCounts()))
                .andExpect(jsonPath("$[0].own").value(true))
                .andExpect(jsonPath("$[1].id").value(otherExpenditure.getId()))
                .andExpect(jsonPath("$[1].imageUrl").value(otherExpenditure.getMainImageUrl()))
                .andExpect(jsonPath("$[1].imageCount").value(otherExpenditure.getImageCounts()))
                .andExpect(jsonPath("$[1].own").value(false));
    }

    @Test
    void 멤버의_배틀_지출목록을_조회한다() throws Exception {
        //given
        final Member member = createMember("oauthId1");
        final Member other = createMember("oauthId2");
        final Battle battle = createBattle();

        join(member, battle);
        join(other, battle);

        final LocalDate battleStart = LocalDate.from(battle.getDuration().getStart());
        createExpenditureWithMainImage(3000L, member.getId(), battleStart.minusDays(1));
        createExpenditureWithMainImage(2000L, other.getId(), battleStart);
        final Expenditure memberExpenditure = createExpenditureWithMainImage(1000L, member.getId(), battleStart);

        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        //when
        //then
        mockMvc.perform(
                        get("/battles/{battleId}/expenditures", battle.getId())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(memberExpenditure.getId()))
                .andExpect(jsonPath("$[0].imageUrl").value(memberExpenditure.getMainImageUrl()))
                .andExpect(jsonPath("$[0].imageCount").value(memberExpenditure.getImageCounts()))
                .andExpect(jsonPath("$[0].own").value(true));
    }

    private Battle createBattle() {
        return battleRepository.save(BattleFixture.initialBattleBuilder().status(BattleStatus.PROGRESS).build());
    }

    private void join(final Member member, final Battle battle) {
        final BattleParticipant battleParticipant = BattleParticipant.normalPlayer(battle.getId(), member.getId());
        battleParticipantRepository.save(battleParticipant);
    }

    private Member createMember(final String oauthId) {
        return memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, oauthId, new MemberNickname("nickname")));
    }

    private Expenditure createExpenditureWithMainImage(final Long amount, final Long memberId, final LocalDate date) {
        return expenditureRepository.save(ExpenditureFixture.simpleWithMainImage(amount, memberId, date));
    }
}
