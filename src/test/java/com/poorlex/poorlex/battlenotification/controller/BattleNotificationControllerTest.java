package com.poorlex.poorlex.battlenotification.controller;

import com.poorlex.poorlex.battle.fixture.BattleFixture;
import com.poorlex.poorlex.battle.service.BattleService;
import com.poorlex.poorlex.battlenotification.domain.BattleNotification;
import com.poorlex.poorlex.battlenotification.domain.BattleNotificationRepository;
import com.poorlex.poorlex.battlenotification.service.dto.request.BattleNotificationCreateRequest;
import com.poorlex.poorlex.battlenotification.service.dto.request.BattleNotificationUpdateRequest;
import com.poorlex.poorlex.config.aws.AWSS3Service;
import com.poorlex.poorlex.member.domain.Member;
import com.poorlex.poorlex.member.domain.MemberNickname;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.member.domain.Oauth2RegistrationId;
import com.poorlex.poorlex.support.IntegrationTest;
import com.poorlex.poorlex.support.MockMultipartFileFixture;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.TestMemberTokenGenerator;
import com.poorlex.poorlex.token.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BattleNotificationControllerTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @Autowired
    private BattleService battleService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BattleNotificationRepository battleNotificationRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private AWSS3Service awss3Service;

    private TestMemberTokenGenerator memberTokenGenerator;

    @BeforeEach
    void setUp() {
        given(awss3Service.uploadMultipartFile(any(), any())).willReturn("imageUrl");
        this.memberTokenGenerator = new TestMemberTokenGenerator(memberRepository, jwtTokenProvider);
    }

    @Test
    void 배틀공지를_등록한다() throws Exception {
        //given
        final Member member = memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        final Long battleId = battleService.create(member.getId(),
                                                   MockMultipartFileFixture.get(),
                                                   BattleFixture.request());
        final String content = "12345678901234567890";
        final BattleNotificationCreateRequest request = new BattleNotificationCreateRequest(content, "imageUrl");
        final String accessToken = memberTokenGenerator.createAccessToken(member);

        //when
        //then
        mockMvc.perform(
                        post("/battles/{battleId}/notification", battleId)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void 배틀공지를_수정한다_이미지_포함() throws Exception {
        //given
        final Member member = memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        final BattleNotification battleNotification = createBattleNotification(member);
        final Long battleId = battleNotification.getBattleId();
        final String newContent = "newContentNewContent";
        final BattleNotificationUpdateRequest request = new BattleNotificationUpdateRequest(newContent, "imageUrl");
        final String accessToken = memberTokenGenerator.createAccessToken(member);

        //when
        //then
        mockMvc.perform(
                        patch("/battles/{battleId}/notification", battleId)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void 배틀공지를_수정한다_이미지_제거() throws Exception {
        //given
        final Member member = memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        final BattleNotification battleNotification = createBattleNotification(member);
        final Long battleId = battleNotification.getBattleId();
        final String newContent = "newContentNewContent";
        final BattleNotificationUpdateRequest request = new BattleNotificationUpdateRequest(newContent, null);
        final String accessToken = memberTokenGenerator.createAccessToken(member);

        //when
        //then
        mockMvc.perform(
                        patch("/battles/{battleId}/notification", battleId)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    private BattleNotification createBattleNotification(final Member member) throws Exception {
        final Long battleId = battleService.create(member.getId(),
                                                   MockMultipartFileFixture.get(),
                                                   BattleFixture.request());
        final String content = "12345678901234567890";
        final BattleNotificationCreateRequest request = new BattleNotificationCreateRequest(content, "imageUrl");
        final String accessToken = memberTokenGenerator.createAccessToken(member);

        mockMvc.perform(
                post("/battles/{battleId}/notification", battleId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        return battleNotificationRepository.findAll().get(0);
    }
}
