package com.poorlex.poorlex.weeklybudget.controller;

import com.poorlex.poorlex.expenditure.domain.Expenditure;
import com.poorlex.poorlex.expenditure.domain.ExpenditureRepository;
import com.poorlex.poorlex.expenditure.fixture.ExpenditureFixture;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberNickname;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId;
import com.poorlex.poorlex.support.IntegrationTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.TestMemberTokenGenerator;
import com.poorlex.poorlex.token.JwtTokenProvider;
import com.poorlex.poorlex.weeklybudget.domain.WeeklyBudget;
import com.poorlex.poorlex.weeklybudget.domain.WeeklyBudgetAmount;
import com.poorlex.poorlex.weeklybudget.domain.WeeklyBudgetDuration;
import com.poorlex.poorlex.weeklybudget.domain.WeeklyBudgetRepository;
import com.poorlex.poorlex.weeklybudget.service.dto.request.WeeklyBudgetCreateRequest;
import com.poorlex.poorlex.weeklybudget.service.dto.request.WeeklyBudgetLeftRequest;
import com.poorlex.poorlex.weeklybudget.service.dto.request.WeeklyBudgetRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WeeklyBudgetControllerTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private WeeklyBudgetRepository weeklyBudgetRepository;

    @Autowired
    private ExpenditureRepository expenditureRepository;

    private TestMemberTokenGenerator memberTokenGenerator;

    @BeforeEach
    void setUp() {
        this.memberTokenGenerator = new TestMemberTokenGenerator(memberRepository, jwtTokenProvider);
    }

    @Test
    void 주간_예산을_생성한다() throws Exception {
        //given
        final WeeklyBudgetCreateRequest request = new WeeklyBudgetCreateRequest(10000L);
        final String accessToken = memberTokenGenerator.createTokenWithNewMember("oauthId");

        //when
        //then
        mockMvc.perform(
                        post("/weekly-budgets")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void ERROR_주간_예산생성시_금액이_0보다_작으면_400_상태코드로_응답한다() throws Exception {
        //given
        final WeeklyBudgetCreateRequest request = new WeeklyBudgetCreateRequest(-1L);
        final String accessToken = memberTokenGenerator.createTokenWithNewMember("oauthId");

        //when
        //then
        mockMvc.perform(
                        post("/weekly-budgets")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void ERROR_주간_예산생성시_금액이_9999999보다_크면_400_상태코드로_응답한다() throws Exception {
        //given
        final WeeklyBudgetCreateRequest request = new WeeklyBudgetCreateRequest(10_000_000L);
        final String accessToken = memberTokenGenerator.createTokenWithNewMember("oauthId");

        //when
        //then
        mockMvc.perform(
                        post("/weekly-budgets")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void 주간_예산을_조회한다_존재할_때() throws Exception {
        //given
        final Member member = createMember("oauthId");
        final WeeklyBudget weaklyBudget = createWeaklyBudget(member.getId(), 10000L);
        final String accessToken = memberTokenGenerator.createAccessToken(member);
        final WeeklyBudgetRequest request = new WeeklyBudgetRequest(weaklyBudget.getDuration().getStart());

        //when
        //then
        mockMvc.perform(
                        get("/weekly-budgets")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exist").value(true))
                .andExpect(jsonPath("$.amount").value(10000))
                .andExpect(jsonPath("$.dday").value(6));
    }

    @Test
    void 주간_예산을_조회한다_존재하지_않을_때() throws Exception {
        //given
        final Member member = createMember("oauthId");
        final String accessToken = memberTokenGenerator.createAccessToken(member);
        final WeeklyBudgetRequest request = new WeeklyBudgetRequest(LocalDate.now());

        //when
        //then
        mockMvc.perform(
                        get("/weekly-budgets")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exist").value(false))
                .andExpect(jsonPath("$.amount").value(0))
                .andExpect(jsonPath("$.dday").value(0));
    }

    @Test
    void 남은_주간_예산을_조회한다() throws Exception {
        //given
        final Member member = createMember("oauthId");
        final WeeklyBudget weaklyBudget = createWeaklyBudget(member.getId(), 10000L);
        final Expenditure expenditure = expend(1000,
                                               member.getId(),
                                               LocalDate.from(weaklyBudget.getDuration().getStart()));

        final String accessToken = memberTokenGenerator.createAccessToken(member);
        final WeeklyBudgetLeftRequest request = new WeeklyBudgetLeftRequest(weaklyBudget.getDuration().getStart());

        //when
        //then
        mockMvc.perform(
                        get("/weekly-budgets/left")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exist").value(true))
                .andExpect(jsonPath("$.amount").value(weaklyBudget.getAmount() - expenditure.getAmount()));
    }

    @Test
    void 남은_주간_예산을_조회한다_등록된_주간_예산이_없을때() throws Exception {
        //given
        final Member member = createMember("oauthId");
        final LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
        expend(1000, member.getId(), LocalDate.from(dateTime));

        final String accessToken = memberTokenGenerator.createAccessToken(member);
        final WeeklyBudgetLeftRequest request = new WeeklyBudgetLeftRequest(LocalDate.from(dateTime));

        //when
        //then
        mockMvc.perform(
                        get("/weekly-budgets/left")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exist").value(false))
                .andExpect(jsonPath("$.amount").value(0));
    }

    private Member createMember(final String oauthId) {
        return memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, oauthId, new MemberNickname("nickname")));
    }

    private WeeklyBudget createWeaklyBudget(final Long memberId, final Long amount) {
        final WeeklyBudget weeklyBudget = WeeklyBudget.withoutId(
                new WeeklyBudgetAmount(amount),
                WeeklyBudgetDuration.current(),
                memberId
        );

        return weeklyBudgetRepository.save(weeklyBudget);
    }

    private Expenditure expend(final int amount, final Long memberId, final LocalDate date) {
        return expenditureRepository.save(ExpenditureFixture.simpleWithMainImage(amount, memberId, date));
    }
}
