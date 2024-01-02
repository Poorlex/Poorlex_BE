package com.poolex.poolex.weeklybudget.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poolex.poolex.auth.domain.Member;
import com.poolex.poolex.auth.domain.MemberNickname;
import com.poolex.poolex.auth.domain.MemberRepository;
import com.poolex.poolex.support.IntegrationTest;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import com.poolex.poolex.support.TestMemberTokenGenerator;
import com.poolex.poolex.token.JwtTokenProvider;
import com.poolex.poolex.weeklybudget.domain.WeeklyBudget;
import com.poolex.poolex.weeklybudget.domain.WeeklyBudgetAmount;
import com.poolex.poolex.weeklybudget.domain.WeeklyBudgetDuration;
import com.poolex.poolex.weeklybudget.domain.WeeklyBudgetRepository;
import com.poolex.poolex.weeklybudget.service.dto.request.WeeklyBudgetCreateRequest;
import com.poolex.poolex.weeklybudget.service.dto.request.WeeklyBudgetRequest;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

class WeeklyBudgetControllerTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private WeeklyBudgetRepository weeklyBudgetRepository;

    private TestMemberTokenGenerator memberTokenGenerator;

    @BeforeEach
    void setUp() {
        this.memberTokenGenerator = new TestMemberTokenGenerator(memberRepository, jwtTokenProvider);
    }

    @Test
    void 주간_예산을_생성한다() throws Exception {
        //given
        final WeeklyBudgetCreateRequest request = new WeeklyBudgetCreateRequest(10000);
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
    void 주간_예산을_조회한다_존재할_때() throws Exception {
        //given
        final Member member = createMember("oauthId");
        final WeeklyBudget weaklyBudget = createWeaklyBudget(member.getId(), 10000);
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
            .andExpect(jsonPath("$.amount").value(10000));
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
            .andExpect(jsonPath("$.amount").value(0));
    }

    private Member createMember(final String oauthId) {
        return memberRepository.save(Member.withoutId(oauthId, new MemberNickname("nickname")));
    }

    private WeeklyBudget createWeaklyBudget(final Long memberId, final int amount) {
        final WeeklyBudget weeklyBudget = WeeklyBudget.withoutId(
            new WeeklyBudgetAmount(amount),
            WeeklyBudgetDuration.current(),
            memberId
        );

        return weeklyBudgetRepository.save(weeklyBudget);
    }
}
