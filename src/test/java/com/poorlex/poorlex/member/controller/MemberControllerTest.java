package com.poorlex.poorlex.member.controller;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    void ERROR_멤버_닉네임수정시_비어있을경우_400_상태코드로_응답한다() throws Exception {
        //given
        final Member member = memberRepository.save(
            Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        final MemberProfileUpdateRequest request = new MemberProfileUpdateRequest("    ", "newDescription");
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        //when
        //then
        mockMvc.perform(
                patch("/member/profile")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void ERROR_멤버_닉네임수정시_2자_미만이면_400_상태코드로_응답한다() throws Exception {
        //given
        final Member member = memberRepository.save(
            Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        final MemberProfileUpdateRequest request = new MemberProfileUpdateRequest("a", "newDescription");
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        //when
        //then
        mockMvc.perform(
                patch("/member/profile")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void ERROR_멤버_닉네임수정시_15자를_초과하면_400_상태코드로_응답한다() throws Exception {
        //given
        final Member member = memberRepository.save(
            Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        final MemberProfileUpdateRequest request = new MemberProfileUpdateRequest("a".repeat(16), "newDescription");
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        //when
        //then
        mockMvc.perform(
                patch("/member/profile")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void ERROR_멤버_닉네임수정시_허용되지_않은_특수기호를_사용하면_400_상태코드로_응답한다() throws Exception {
        //given
        final Member member = memberRepository.save(
            Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        final MemberProfileUpdateRequest request = new MemberProfileUpdateRequest("!@#$%^&*()", "newDescription");
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        //when
        //then
        mockMvc.perform(
                patch("/member/profile")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void ERROR_멤버_닉네임수정시_허용되지_않은_문자를_사용하면_400_상태코드로_응답한다() throws Exception {
        //given
        final Member member = memberRepository.save(
            Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        final MemberProfileUpdateRequest request = new MemberProfileUpdateRequest("hello😃", "newDescription");
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        //when
        //then
        mockMvc.perform(
                patch("/member/profile")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void ERROR_멤버_닉네임수정시_소개가_비어있으면_400_상태코드로_응답한다() throws Exception {
        //given
        final Member member = memberRepository.save(
            Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        final MemberProfileUpdateRequest request = new MemberProfileUpdateRequest("nickname", "      ");
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        //when
        //then
        mockMvc.perform(
                patch("/member/profile")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void ERROR_멤버_닉네임수정시_소개가_2자미만이면_400_상태코드로_응답한다() throws Exception {
        //given
        final Member member = memberRepository.save(
            Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        final MemberProfileUpdateRequest request = new MemberProfileUpdateRequest("nickname", "a");
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        //when
        //then
        mockMvc.perform(
                patch("/member/profile")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void ERROR_멤버_닉네임수정시_소개가_300자를_초과하면_400_상태코드로_응답한다() throws Exception {
        //given
        final Member member = memberRepository.save(
            Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        final MemberProfileUpdateRequest request = new MemberProfileUpdateRequest("nickname", "a".repeat(301));
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        //when
        //then
        mockMvc.perform(
                patch("/member/profile")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());
    }
}
