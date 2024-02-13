package com.poorlex.poorlex.member.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poorlex.poorlex.member.domain.Member;
import com.poorlex.poorlex.member.domain.MemberNickname;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.member.domain.Oauth2RegistrationId;
import com.poorlex.poorlex.member.service.dto.request.MemberProfileUpdateRequest;
import com.poorlex.poorlex.support.IntegrationTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.token.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

class MemberControllerTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    //JPA 변경 감지를 통한 업데이트가 진행되기 때문에 같은 EntityManager 사용하도록 트랜잭션 전파
    @Transactional
    void 멤버의_프로필을_변경한다() throws Exception {
        //given
        final Member member = memberRepository.save(
            Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        final MemberProfileUpdateRequest request = new MemberProfileUpdateRequest("newNickname", "newDescription");
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        //when
        mockMvc.perform(
                patch("/member/profile")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andDo(print())
            .andExpect(status().isOk());

        //then
        final Member updatedMember = memberRepository.findById(member.getId()).get();
        assertThat(updatedMember.getNickname()).isEqualTo(request.getNickname());
        assertThat(updatedMember.getDescription()).isPresent()
            .get()
            .isEqualTo(request.getDescription());
    }
}
