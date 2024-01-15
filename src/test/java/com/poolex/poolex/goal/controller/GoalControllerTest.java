package com.poolex.poolex.goal.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.poolex.poolex.goal.domain.Goal;
import com.poolex.poolex.goal.domain.GoalAmount;
import com.poolex.poolex.goal.domain.GoalDuration;
import com.poolex.poolex.goal.domain.GoalName;
import com.poolex.poolex.goal.domain.GoalRepository;
import com.poolex.poolex.goal.domain.GoalStatus;
import com.poolex.poolex.goal.domain.GoalType;
import com.poolex.poolex.goal.service.dto.request.GoalCreateRequest;
import com.poolex.poolex.member.domain.Member;
import com.poolex.poolex.member.domain.MemberNickname;
import com.poolex.poolex.member.domain.MemberRepository;
import com.poolex.poolex.support.IntegrationTest;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import com.poolex.poolex.support.TestMemberTokenGenerator;
import com.poolex.poolex.token.JwtTokenProvider;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class GoalControllerTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private GoalRepository goalRepository;

    private TestMemberTokenGenerator testMemberTokenGenerator;

    @BeforeEach
    void setUp() {
        this.testMemberTokenGenerator = new TestMemberTokenGenerator(memberRepository, jwtTokenProvider);
    }

    @Test
    void 목표를_생성한다() throws Exception {
        //given
        final GoalCreateRequest request = new GoalCreateRequest(
            GoalType.REST_AND_REFRESH.name(),
            "목표명",
            10000,
            LocalDate.now().minusDays(1),
            LocalDate.now()
        );
        final String accessToken = testMemberTokenGenerator.createTokenWithNewMember("oauthId");

        //when
        //then
        mockMvc.perform(
                post("/goals")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void 멤버의_목표들의_Id를_조회한다() throws Exception {
        //given
        final Member member = memberRepository.save(Member.withoutId("oauthId", new MemberNickname("nickname")));

        final String accessToken = testMemberTokenGenerator.createAccessToken(member);
        final Goal goal1 = saveGoalWithMemberId(member.getId());
        final Goal goal2 = saveGoalWithMemberId(member.getId());
        final Goal goal3 = saveGoalWithMemberId(member.getId());

        //when
        //then
        mockMvc.perform(
                get("/goals")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            )
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].goalId").value(goal1.getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].goalId").value(goal2.getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$[2].goalId").value(goal3.getId()));
    }

    private Goal saveGoalWithMemberId(final Long memberId) {
        final GoalName goalName = new GoalName("목표명");
        final GoalType goalType = GoalType.REST_AND_REFRESH;
        final GoalDuration goalDuration = new GoalDuration(LocalDate.now().minusDays(1), LocalDate.now());
        final GoalAmount goalAmount = new GoalAmount(10000);
        final Goal goal = Goal.withoutId(memberId, goalType, goalName, goalAmount, goalDuration, GoalStatus.PROGRESS);

        return goalRepository.save(goal);
    }
}
