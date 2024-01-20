package com.poorlex.poorlex.goal.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.poorlex.poorlex.goal.domain.Goal;
import com.poorlex.poorlex.goal.domain.GoalAmount;
import com.poorlex.poorlex.goal.domain.GoalDuration;
import com.poorlex.poorlex.goal.domain.GoalName;
import com.poorlex.poorlex.goal.domain.GoalRepository;
import com.poorlex.poorlex.goal.domain.GoalStatus;
import com.poorlex.poorlex.goal.domain.GoalType;
import com.poorlex.poorlex.goal.service.dto.request.GoalCreateRequest;
import com.poorlex.poorlex.member.domain.Member;
import com.poorlex.poorlex.member.domain.MemberNickname;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.support.IntegrationTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.TestMemberTokenGenerator;
import com.poorlex.poorlex.token.JwtTokenProvider;
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
    void 목표_타입_목록을_조회한다() throws Exception {
        //given
        final GoalType[] goalTypes = GoalType.values();

        //when
        //then
        mockMvc.perform(get("/goals/types"))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(goalTypes.length));
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
