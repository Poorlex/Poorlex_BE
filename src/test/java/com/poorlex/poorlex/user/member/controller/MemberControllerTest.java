package com.poorlex.poorlex.user.member.controller;

import com.poorlex.poorlex.support.IntegrationTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.auth.service.JwtTokenProvider;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberNickname;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId;
import com.poorlex.poorlex.user.member.service.dto.request.MemberProfileUpdateRequest;
import io.jsonwebtoken.impl.DefaultClaims;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.when;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberControllerTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void 멤버의_프로필을_변경시_200_상태코드로_응답한다() throws Exception {
        //given
        final Long 회원_ID = 1L;
        MOCKING_토큰이_회원_ID를_가진_클레임을_반환하도록_한다(회원_ID);
        MOCKING_토큰과_클레임이_회원_ID를_반환하도록_한다(회원_ID);
        MOCKING_ID로_회원조회시_동일한_ID를_가지는_임의의_회원을_반환하도록_한다(회원_ID);
        MOCKING_토큰이_없더라도_회원을_반환한다(회원_ID);

        final String 회원_액세스_토큰 = "access_token";
        final MemberProfileUpdateRequest 변경_요청 = new MemberProfileUpdateRequest("닉네임", "소개");

        //when
        //then
        mockMvc.perform(
                        patch("/member/profile")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + 회원_액세스_토큰)
                                .content(objectMapper.writeValueAsString(변경_요청))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void ERROR_프로필_변경시_닉네임이_빈칸으로_이루어져_있으면_400_상태코드로_응답한다() throws Exception {
        //given
        final Long 회원_ID = 1L;
        MOCKING_토큰이_회원_ID를_가진_클레임을_반환하도록_한다(회원_ID);
        MOCKING_토큰과_클레임이_회원_ID를_반환하도록_한다(회원_ID);
        MOCKING_ID로_회원조회시_동일한_ID를_가지는_임의의_회원을_반환하도록_한다(회원_ID);
        MOCKING_토큰이_없더라도_회원을_반환한다(회원_ID);

        final String 회원_액세스_토큰 = "access_token";
        final MemberProfileUpdateRequest 변경_요청 = new MemberProfileUpdateRequest("    ", "소개");

        //when
        //then
        mockMvc.perform(
                        patch("/member/profile")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + 회원_액세스_토큰)
                                .content(objectMapper.writeValueAsString(변경_요청))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void ERROR_멤버_닉네임수정시_2자_미만이면_400_상태코드로_응답한다() throws Exception {
        //given
        final Long 회원_ID = 1L;
        MOCKING_토큰이_회원_ID를_가진_클레임을_반환하도록_한다(회원_ID);
        MOCKING_토큰과_클레임이_회원_ID를_반환하도록_한다(회원_ID);
        MOCKING_ID로_회원조회시_동일한_ID를_가지는_임의의_회원을_반환하도록_한다(회원_ID);
        MOCKING_토큰이_없더라도_회원을_반환한다(회원_ID);

        final String 회원_액세스_토큰 = "access_token";
        final MemberProfileUpdateRequest 변경_요청 = new MemberProfileUpdateRequest("a", "소개");

        //when
        //then
        mockMvc.perform(
                        patch("/member/profile")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + 회원_액세스_토큰)
                                .content(objectMapper.writeValueAsString(변경_요청))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void ERROR_멤버_닉네임수정시_15자를_초과하면_400_상태코드로_응답한다() throws Exception {
        //given
        final Long 회원_ID = 1L;
        MOCKING_토큰이_회원_ID를_가진_클레임을_반환하도록_한다(회원_ID);
        MOCKING_토큰과_클레임이_회원_ID를_반환하도록_한다(회원_ID);
        MOCKING_ID로_회원조회시_동일한_ID를_가지는_임의의_회원을_반환하도록_한다(회원_ID);
        MOCKING_토큰이_없더라도_회원을_반환한다(회원_ID);

        final String 회원_액세스_토큰 = "access_token";
        final MemberProfileUpdateRequest 변경_요청 = new MemberProfileUpdateRequest("a".repeat(16), "소개");

        //when
        //then
        mockMvc.perform(
                        patch("/member/profile")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + 회원_액세스_토큰)
                                .content(objectMapper.writeValueAsString(변경_요청))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void ERROR_멤버_닉네임수정시_소개가_비어있으면_400_상태코드로_응답한다() throws Exception {
        //given
        final Long 회원_ID = 1L;
        MOCKING_토큰이_회원_ID를_가진_클레임을_반환하도록_한다(회원_ID);
        MOCKING_토큰과_클레임이_회원_ID를_반환하도록_한다(회원_ID);
        MOCKING_ID로_회원조회시_동일한_ID를_가지는_임의의_회원을_반환하도록_한다(회원_ID);
        MOCKING_토큰이_없더라도_회원을_반환한다(회원_ID);

        final String 회원_액세스_토큰 = "access_token";
        final MemberProfileUpdateRequest 변경_요청 = new MemberProfileUpdateRequest("닉네임", "    ");

        //when
        //then
        mockMvc.perform(
                        patch("/member/profile")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + 회원_액세스_토큰)
                                .content(objectMapper.writeValueAsString(변경_요청))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void ERROR_멤버_닉네임수정시_소개가_300자를_초과하면_400_상태코드로_응답한다() throws Exception {
        //given
        final Long 회원_ID = 1L;
        MOCKING_토큰이_회원_ID를_가진_클레임을_반환하도록_한다(회원_ID);
        MOCKING_토큰과_클레임이_회원_ID를_반환하도록_한다(회원_ID);
        MOCKING_ID로_회원조회시_동일한_ID를_가지는_임의의_회원을_반환하도록_한다(회원_ID);
        MOCKING_토큰이_없더라도_회원을_반환한다(회원_ID);

        final String 회원_액세스_토큰 = "access_token";
        final MemberProfileUpdateRequest 변경_요청 = new MemberProfileUpdateRequest("닉네임", "a".repeat(301));

        //when
        //then
        mockMvc.perform(
                        patch("/member/profile")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + 회원_액세스_토큰)
                                .content(objectMapper.writeValueAsString(변경_요청))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    private void MOCKING_ID로_회원조회시_동일한_ID를_가지는_임의의_회원을_반환하도록_한다(final Long 회원_ID) {
        final Member 스플릿 = new Member(회원_ID, Oauth2RegistrationId.APPLE, "고유 ID", new MemberNickname("스플릿"));
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(스플릿));
    }

    private void MOCKING_토큰이_회원_ID를_가진_클레임을_반환하도록_한다(final Long 회원_ID) {
        final DefaultClaims 토큰_정보 = new DefaultClaims(Map.of("memberId", 회원_ID));
        when(jwtTokenProvider.getPayload(any())).thenReturn(토큰_정보);
    }

    private void MOCKING_토큰과_클레임이_회원_ID를_반환하도록_한다(final Long 회원_ID) {
        when(jwtTokenProvider.getPayload(anyString(), anyString(), any())).thenReturn(회원_ID);
    }

    private void MOCKING_토큰이_없더라도_회원을_반환한다(final Long 회원_ID) {
        when(memberRepository.existsById(회원_ID)).thenReturn(true);
    }
}
