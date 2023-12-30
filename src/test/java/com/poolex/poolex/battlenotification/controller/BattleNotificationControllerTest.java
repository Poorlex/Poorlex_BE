package com.poolex.poolex.battlenotification.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poolex.poolex.battle.fixture.BattleCreateRequestFixture;
import com.poolex.poolex.battle.service.BattleService;
import com.poolex.poolex.battlenotification.domain.BattleNotification;
import com.poolex.poolex.battlenotification.domain.BattleNotificationRepository;
import com.poolex.poolex.battlenotification.service.dto.request.BattleNotificationCreateRequest;
import com.poolex.poolex.battlenotification.service.dto.request.BattleNotificationUpdateRequest;
import com.poolex.poolex.member.domain.Member;
import com.poolex.poolex.member.domain.MemberNickname;
import com.poolex.poolex.member.domain.MemberRepository;
import com.poolex.poolex.support.DataCleaner;
import com.poolex.poolex.support.DataCleanerExtension;
import com.poolex.poolex.support.IntegrationTest;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import com.poolex.poolex.support.TestMemberTokenGenerator;
import com.poolex.poolex.token.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@ExtendWith(DataCleanerExtension.class)
class BattleNotificationControllerTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @Autowired
    private DataCleaner dataCleaner;

    @Autowired
    private BattleService battleService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BattleNotificationRepository battleNotificationRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private TestMemberTokenGenerator memberTokenGenerator;

    @BeforeEach
    void setUp() {
        this.memberTokenGenerator = new TestMemberTokenGenerator(memberRepository, jwtTokenProvider);
        dataCleaner.clear();
    }

    @Test
    void 배틀공지를_등록한다() throws Exception {
        //given
        final Member member = memberRepository.save(Member.withoutId(new MemberNickname("nickname")));
        final Long battleId = battleService.create(member.getId(), BattleCreateRequestFixture.simple());
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
        final Member member = memberRepository.save(Member.withoutId(new MemberNickname("nickname")));
        final BattleNotification battleNotification = createBattleNotification(member);
        final Long battleId = battleNotification.getBattleId();
        final String newContent = "newContentNewContent";
        final BattleNotificationUpdateRequest request =
            new BattleNotificationUpdateRequest(battleId, newContent, "imageUrl");
        final String accessToken = memberTokenGenerator.createAccessToken(member);

        //when
        //then
        mockMvc.perform(
                put("/battles/{battleId}/notification", battleId)
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
        final Member member = memberRepository.save(Member.withoutId(new MemberNickname("nickname")));
        final BattleNotification battleNotification = createBattleNotification(member);
        final Long battleId = battleNotification.getBattleId();
        final String newContent = "newContentNewContent";
        final BattleNotificationUpdateRequest request =
            new BattleNotificationUpdateRequest(battleId, newContent, null);
        final String accessToken = memberTokenGenerator.createAccessToken(member);

        //when
        //then
        mockMvc.perform(
                put("/battles/{battleId}/notification", battleId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    private BattleNotification createBattleNotification(final Member member) throws Exception {
        final Long battleId = battleService.create(member.getId(), BattleCreateRequestFixture.simple());
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
