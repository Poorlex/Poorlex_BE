package com.poolex.poolex.goal.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.poolex.poolex.goal.domain.GoalType;
import com.poolex.poolex.goal.service.dto.request.GoalCreateRequest;
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
}
