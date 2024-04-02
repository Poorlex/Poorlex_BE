package com.poorlex.poorlex.auth.controller;

import com.poorlex.poorlex.auth.service.dto.request.LoginRequest;
import com.poorlex.poorlex.auth.service.dto.response.LoginTokenResponse;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberNickname;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId;
import com.poorlex.poorlex.support.IntegrationTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.token.JwtTokenProvider;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void 가입한_사용자가_로그인을_진행한다() throws Exception {
        //given
        final Member member = createMember("oauthId", "nickname");
        final LoginRequest request = new LoginRequest(member.getOauthId(), member.getNickname());

        //when
        final MvcResult mvcResult = mockMvc.perform(
                        post("/login")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        //then
        final Long tokenMemberId = extractMemberIdFromAccessToken(mvcResult);
        assertThat(tokenMemberId).isEqualTo(member.getId());
    }

    @Test
    void 새로운_사용자가_로그인을_진행한다() throws Exception {
        //given
        final LoginRequest request = new LoginRequest("newOauthId", "newNickname");

        //when
        final MvcResult mvcResult = mockMvc.perform(
                        post("/login")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        //then
        final Member createdMember = memberRepository.findByOauthId(request.getOauthId())
                .orElseThrow(IllegalArgumentException::new);
        final Long tokenMemberId = extractMemberIdFromAccessToken(mvcResult);

        assertThat(tokenMemberId).isEqualTo(createdMember.getId());
    }

    private Member createMember(final String oauthId, final String nickname) {
        return memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, oauthId, new MemberNickname(nickname)));
    }

    private Long extractMemberIdFromAccessToken(final MvcResult result) throws Exception {
        final String responseBody = result.getResponse().getContentAsString();
        final LoginTokenResponse response = objectMapper.readValue(responseBody, LoginTokenResponse.class);

        return jwtTokenProvider.getPayload(response.getAccessToken())
                .get("memberId", Long.class);
    }
}
