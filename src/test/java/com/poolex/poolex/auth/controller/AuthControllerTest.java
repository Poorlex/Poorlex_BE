package com.poolex.poolex.auth.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poolex.poolex.auth.domain.Member;
import com.poolex.poolex.auth.domain.MemberNickname;
import com.poolex.poolex.auth.domain.MemberRepository;
import com.poolex.poolex.auth.service.dto.request.LoginRequest;
import com.poolex.poolex.auth.service.dto.response.LoginTokenResponse;
import com.poolex.poolex.support.IntegrationTest;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import com.poolex.poolex.token.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

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
        return memberRepository.save(Member.withoutId(oauthId, new MemberNickname(nickname)));
    }

    private Long extractMemberIdFromAccessToken(final MvcResult result) throws Exception {
        final String responseBody = result.getResponse().getContentAsString();
        final LoginTokenResponse response = objectMapper.readValue(responseBody, LoginTokenResponse.class);

        return jwtTokenProvider.getPayload(response.getAccessToken())
            .get("memberId", Long.class);
    }
}
