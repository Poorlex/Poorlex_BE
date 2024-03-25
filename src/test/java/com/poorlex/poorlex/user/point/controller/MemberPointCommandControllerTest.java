package com.poorlex.poorlex.user.point.controller;

import com.poorlex.poorlex.support.IntegrationTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.token.JwtTokenProvider;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberNickname;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId;
import com.poorlex.poorlex.user.point.service.dto.request.PointCreateRequest;
import io.jsonwebtoken.impl.DefaultClaims;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("회원 포인트 관리 컨트롤러 테스트")
class MemberPointCommandControllerTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void 멤버포인트를_생성한다() throws Exception {
        //given
        final Long 회원_ID = 1L;
        MOCKING_토큰이_회원_ID를_가진_클레임을_반환하도록_한다(회원_ID);
        MOCKING_ID로_회원조회시_동일한_ID를_가지는_임의의_회원을_반환하도록_한다(회원_ID);
        MOCKING_멤버_존재여부를_확인시_참을_반환하도록_한다();

        final String 회원_액세스_토큰 = "access_token";
        final PointCreateRequest 포인트_지급_요청 = new PointCreateRequest(10);

        //when
        //then
        mockMvc.perform(
                        post("/point")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + 회원_액세스_토큰)
                                .content(objectMapper.writeValueAsString(포인트_지급_요청))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    private void MOCKING_ID로_회원조회시_동일한_ID를_가지는_임의의_회원을_반환하도록_한다(final Long 회원_ID) {
        final Member 스플릿 = new Member(회원_ID, Oauth2RegistrationId.APPLE, "고유 ID", new MemberNickname("스플릿"));
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(스플릿));
    }

    private void MOCKING_토큰이_회원_ID를_가진_클레임을_반환하도록_한다(final Long 회원_ID) {
        final DefaultClaims 토큰_정보 = new DefaultClaims(Map.of("memberId", 회원_ID));
        when(jwtTokenProvider.getPayload(any())).thenReturn(토큰_정보);
    }

    private void MOCKING_멤버_존재여부를_확인시_참을_반환하도록_한다() {
        when(memberRepository.existsById(anyLong())).thenReturn(true);
    }
}
