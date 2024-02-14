package com.poorlex.poorlex.point.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poorlex.poorlex.member.domain.Member;
import com.poorlex.poorlex.member.domain.MemberLevel;
import com.poorlex.poorlex.member.domain.MemberNickname;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.member.domain.Oauth2RegistrationId;
import com.poorlex.poorlex.point.domain.MemberPoint;
import com.poorlex.poorlex.point.domain.MemberPointRepository;
import com.poorlex.poorlex.point.domain.Point;
import com.poorlex.poorlex.point.service.dto.request.PointCreateRequest;
import com.poorlex.poorlex.support.IntegrationTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.TestMemberTokenGenerator;
import com.poorlex.poorlex.token.JwtTokenProvider;
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

    @Autowired
    private MemberPointRepository memberPointRepository;

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

    @Test
    void 멤버포인트의_합과_레벨을_조회한다() throws Exception {
        //given
        final Member member = createMember("oauthId");
        final MemberPoint memberPoint = createMemberPoint(10, member);
        final String accessToken = memberTokenGenerator.createAccessToken(member);

        //when
        //then
        mockMvc.perform(
                get("/points")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalPoint").value(memberPoint.getPoint()))
            .andExpect(jsonPath("$.level").value(MemberLevel.LEVEL_1.getNumber()));
    }

    @Test
    void 멤버_레벨바에_필요한_정보를_조회한다() throws Exception {
        //given
        final Member member = createMember("oauthId");
        final MemberPoint memberPoint = createMemberPoint(10, member);
        final String accessToken = memberTokenGenerator.createAccessToken(member);

        //when
        //then
        mockMvc.perform(
                get("/points/level-bar")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.levelRange").value(MemberLevel.LEVEL_1.getLevelRange()))
            .andExpect(jsonPath("$.currentPoint").value(memberPoint.getPoint()))
            .andExpect(jsonPath("$.recentPoint").value(memberPoint.getPoint()));
    }

    private Member createMember(final String oauthId) {
        return memberRepository.save(
            Member.withoutId(Oauth2RegistrationId.APPLE, oauthId, new MemberNickname("nickname")));
    }

    private MemberPoint createMemberPoint(final int point, final Member member) {
        return memberPointRepository.save(MemberPoint.withoutId(new Point(point), member.getId()));
    }
}
