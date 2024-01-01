package com.poolex.poolex.point.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poolex.poolex.auth.domain.MemberRepository;
import com.poolex.poolex.point.service.dto.PointCreateRequest;
import com.poolex.poolex.support.IntegrationTest;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import com.poolex.poolex.support.TestMemberTokenGenerator;
import com.poolex.poolex.token.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

class MemberPointControllerTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    private TestMemberTokenGenerator memberTokenGenerator;

    @BeforeEach
    void setUp() {
        this.memberTokenGenerator = new TestMemberTokenGenerator(memberRepository, jwtTokenProvider);
    }

    @Test
    void 멤버포인트를_생성한다() throws Exception {
        //given
        final PointCreateRequest pointCreateRequest = new PointCreateRequest(10);
        final String accessToken = memberTokenGenerator.createTokenWithNewMember("nickname");

        //when
        //then
        mockMvc.perform(
                post("/points")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .content(objectMapper.writeValueAsString(pointCreateRequest))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isCreated());
    }
}
