package com.poorlex.poorlex.battle.notification.controller;

import com.poorlex.poorlex.battle.battle.fixture.BattleFixture;
import com.poorlex.poorlex.battle.battle.service.BattleImageService;
import com.poorlex.poorlex.battle.battle.service.BattleService;
import com.poorlex.poorlex.battle.notification.domain.BattleNotification;
import com.poorlex.poorlex.battle.notification.domain.BattleNotificationRepository;
import com.poorlex.poorlex.battle.notification.service.dto.request.BattleNotificationCreateRequest;
import com.poorlex.poorlex.battle.notification.service.dto.request.BattleNotificationUpdateRequest;
import com.poorlex.poorlex.consumption.weeklybudget.domain.WeeklyBudget;
import com.poorlex.poorlex.consumption.weeklybudget.domain.WeeklyBudgetAmount;
import com.poorlex.poorlex.consumption.weeklybudget.domain.WeeklyBudgetRepository;
import com.poorlex.poorlex.support.IntegrationTest;
import com.poorlex.poorlex.support.MockMultipartFileFixture;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.TestMemberTokenGenerator;
import com.poorlex.poorlex.auth.service.JwtTokenProvider;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberNickname;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId;
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
    private WeeklyBudgetRepository weeklyBudgetRepository;

    @Autowired
    private BattleNotificationRepository battleNotificationRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private BattleImageService imageService;

    private TestMemberTokenGenerator memberTokenGenerator;

    @BeforeEach
    void setUp() {
        given(imageService.saveAndReturnPath(any(), any())).willReturn("imageUrl");
        this.memberTokenGenerator = new TestMemberTokenGenerator(memberRepository, jwtTokenProvider);
    }

    @Test
    void 배틀공지를_등록한다() throws Exception {
        //given
        final Member member = memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        createWeeklyBudget(member.getId());
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
        createWeeklyBudget(member.getId());
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
        createWeeklyBudget(member.getId());
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

    private void createWeeklyBudget(Long memberId) {
        WeeklyBudget weeklyBudget = WeeklyBudget.withoutId(new WeeklyBudgetAmount(100000L), memberId);
        weeklyBudgetRepository.save(weeklyBudget);
    }
}
