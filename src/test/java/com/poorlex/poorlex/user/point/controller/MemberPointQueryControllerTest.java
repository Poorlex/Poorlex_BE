package com.poorlex.poorlex.user.point.controller;

import com.poorlex.poorlex.support.IntegrationTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.auth.service.JwtTokenProvider;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberNickname;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId;
import com.poorlex.poorlex.user.point.domain.MemberPoint;
import com.poorlex.poorlex.user.point.domain.MemberPointRepository;
import com.poorlex.poorlex.user.point.domain.Point;
import io.jsonwebtoken.impl.DefaultClaims;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberPointQueryControllerTest extends IntegrationTest implements ReplaceUnderScoreTest {


    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private MemberPointRepository memberPointRepository;

    @Test
    void 멤버포인트의_합과_레벨을_조회한다() throws Exception {
        //given
        final Long 회원_ID = 1L;
        final int 회원_총_포인트 = 10;

        MOCKING_토큰이_회원_ID를_가진_클레임을_반환하도록_한다(회원_ID);
        MOCKING_ID로_회원조회시_동일한_ID를_가지는_임의의_회원을_반환하도록_한다(회원_ID);
        MOCKING_회원의_총_포인트_조회시_다음값으로_반환하도록_한다(회원_총_포인트);

        final String 회원_액세스_토큰 = "access_token";

        //when
        //then
        mockMvc.perform(
                        get("/point")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + 회원_액세스_토큰)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void 멤버_레벨바에_필요한_정보를_조회한다() throws Exception {
        //given
        final Long 회원_ID = 1L;
        final int 회원_총_포인트 = 30;
        final int 회원이_최근_얻은_포인트 = 10;

        MOCKING_토큰이_회원_ID를_가진_클레임을_반환하도록_한다(회원_ID);
        MOCKING_ID로_회원조회시_동일한_ID를_가지는_임의의_회원을_반환하도록_한다(회원_ID);
        MOCKING_회원의_총_포인트_조회시_다음값으로_반환하도록_한다(회원_총_포인트);
        MOCKING_회원의_최근_포인트_조회시_다음값으로_반환하도록_한다(회원이_최근_얻은_포인트);

        final String 회원_액세스_토큰 = "access_token";

        //when
        //then
        mockMvc.perform(
                        get("/point/level-bar")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + 회원_액세스_토큰)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    private void MOCKING_ID로_회원조회시_동일한_ID를_가지는_임의의_회원을_반환하도록_한다(final Long 회원_ID) {
        final Member 스플릿 = new Member(회원_ID, Oauth2RegistrationId.APPLE, "고유 ID", new MemberNickname("스플릿"));
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(스플릿));
    }

    private void MOCKING_토큰이_회원_ID를_가진_클레임을_반환하도록_한다(final Long 회원_ID) {
        final DefaultClaims 토큰_정보 = new DefaultClaims(Map.of("memberId", 회원_ID));
        when(jwtTokenProvider.getPayload(any())).thenReturn(토큰_정보);
    }

    private void MOCKING_회원의_총_포인트_조회시_다음값으로_반환하도록_한다(final int sumPoint) {
        when(memberPointRepository.findSumByMemberId(anyLong())).thenReturn(sumPoint);
    }

    private void MOCKING_회원의_최근_포인트_조회시_다음값으로_반환하도록_한다(final int sumPoint) {
        when(memberPointRepository.findFirstByMemberIdOrderByCreatedAt(anyLong()))
                .thenReturn(Optional.of(new MemberPoint(1L, new Point(sumPoint), 1L)));
    }
}
