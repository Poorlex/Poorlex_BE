package com.poorlex.poorlex.goal.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poorlex.poorlex.goal.domain.Goal;
import com.poorlex.poorlex.goal.domain.GoalAmount;
import com.poorlex.poorlex.goal.domain.GoalDuration;
import com.poorlex.poorlex.goal.domain.GoalDurationType;
import com.poorlex.poorlex.goal.domain.GoalName;
import com.poorlex.poorlex.goal.domain.GoalRepository;
import com.poorlex.poorlex.goal.domain.GoalStatus;
import com.poorlex.poorlex.goal.domain.GoalType;
import com.poorlex.poorlex.goal.service.dto.request.GoalCreateRequest;
import com.poorlex.poorlex.goal.service.dto.request.GoalUpdateRequest;
import com.poorlex.poorlex.goal.service.dto.request.MemberGoalRequest;
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
            .andExpect(status().isCreated());
    }

    @Test
    void 목표를_수정한다() throws Exception {
        //given
        final Member member = memberRepository.save(Member.withoutId("oauthId", new MemberNickname("nickname")));
        final Goal goal = saveGoalWithMemberId(member.getId());
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());
        final GoalUpdateRequest request = new GoalUpdateRequest(
            GoalType.REST_AND_REFRESH.name(),
            "목표명",
            10000,
            LocalDate.now().minusDays(1),
            LocalDate.now()
        );

        //when
        //then
        mockMvc.perform(
                patch("/goals/{goalId}", goal.getId())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    void 목표를_삭제한다() throws Exception {
        //given
        final Member member = memberRepository.save(Member.withoutId("oauthId", new MemberNickname("nickname")));
        final Goal goal = saveGoalWithMemberId(member.getId());
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        //when
        //then
        mockMvc.perform(
                delete("/goals/{goalId}", goal.getId())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            ).andDo(print())
            .andExpect(status().isNoContent());
    }

    @Test
    void 목표를_완료한다() throws Exception {
        //given
        final Member member = memberRepository.save(Member.withoutId("oauthId", new MemberNickname("nickname")));
        final Goal goal = saveGoalWithMemberId(member.getId());
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        //when
        //then
        mockMvc.perform(
                post("/goals/{goalId}/finish", goal.getId())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            ).andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    void 목표_타입_목록을_조회한다() throws Exception {
        //given
        final GoalType[] goalTypes = GoalType.values();

        //when
        //then
        mockMvc.perform(get("/goals/types"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(goalTypes.length));
    }

    @Test
    void 회원이_진행중인_목표목록을_조회한다() throws Exception {
        //given
        final Member member = memberRepository.save(Member.withoutId("oauthId", new MemberNickname("nickname")));
        final LocalDate requestDate = LocalDate.now();
        final Goal goal = saveGoalWithMemberId(member.getId(), requestDate.minusYears(1), requestDate.plusDays(10));
        final MemberGoalRequest request = new MemberGoalRequest("PROGRESS", requestDate);
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        //when
        //then
        mockMvc.perform(
                get("/goals")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].name").value(goal.getName()))
            .andExpect(jsonPath("$[0].durationType").value(GoalDurationType.MIDDLE.name()))
            .andExpect(jsonPath("$[0].amount").value(goal.getAmount()))
            .andExpect(jsonPath("$[0].dayLeft").value(10))
            .andExpect(jsonPath("$[0].monthLeft").value(goal.getMonthLeft(requestDate)))
            .andExpect(jsonPath("$[0].startDate").value(goal.getStartDate().toString()))
            .andExpect(jsonPath("$[0].endDate").value(goal.getEndDate().toString()));
    }

    @Test
    void 회원이_완료한_목표목록을_조회한다() throws Exception {
        //given
        final Member member = memberRepository.save(Member.withoutId("oauthId", new MemberNickname("nickname")));
        final LocalDate requestDate = LocalDate.now();
        final Goal goal = saveGoalWithMemberId(member.getId(), requestDate.minusYears(1), requestDate.plusYears(3));
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());
        sendRequestToFinishGoal(goal.getId(), accessToken);
        final MemberGoalRequest request = new MemberGoalRequest("FINISH", requestDate);

        //when
        //then
        mockMvc.perform(
                get("/goals")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].name").value(goal.getName()))
            .andExpect(jsonPath("$[0].durationType").value(GoalDurationType.MIDDLE.name()))
            .andExpect(jsonPath("$[0].amount").value(goal.getAmount()))
            .andExpect(jsonPath("$[0].dayLeft").value(0))
            .andExpect(jsonPath("$[0].monthLeft").value(0))
            .andExpect(jsonPath("$[0].startDate").value(goal.getStartDate().toString()))
            .andExpect(jsonPath("$[0].endDate").value(goal.getEndDate().toString()));
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
                get("/goals/ids")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0].goalId").value(goal1.getId()))
            .andExpect(jsonPath("$[1].goalId").value(goal2.getId()))
            .andExpect(jsonPath("$[2].goalId").value(goal3.getId()));
    }

    private Goal saveGoalWithMemberId(final Long memberId) {
        final GoalName goalName = new GoalName("목표명");
        final GoalType goalType = GoalType.REST_AND_REFRESH;
        final GoalDuration goalDuration = new GoalDuration(LocalDate.now().minusDays(1), LocalDate.now());
        final GoalAmount goalAmount = new GoalAmount(10000);
        final Goal goal = Goal.withoutId(memberId, goalType, goalName, goalAmount, goalDuration, GoalStatus.PROGRESS);

        return goalRepository.save(goal);
    }

    private Goal saveGoalWithMemberId(final Long memberId, final LocalDate start, final LocalDate end) {
        final GoalName goalName = new GoalName("목표명");
        final GoalType goalType = GoalType.REST_AND_REFRESH;
        final GoalDuration goalDuration = new GoalDuration(start, end);
        final GoalAmount goalAmount = new GoalAmount(10000);
        final Goal goal = Goal.withoutId(memberId, goalType, goalName, goalAmount, goalDuration, GoalStatus.PROGRESS);

        return goalRepository.save(goal);
    }

    private void sendRequestToFinishGoal(final Long goalId, final String accessToken) throws Exception {
        mockMvc.perform(
            post("/goals/{goalId}/finish", goalId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        );
    }
}
