package com.poorlex.poorlex.friend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poorlex.poorlex.alarm.memberalram.service.MemberAlarmEventHandler;
import com.poorlex.poorlex.friend.domain.Friend;
import com.poorlex.poorlex.friend.domain.FriendRepository;
import com.poorlex.poorlex.friend.service.dto.request.FriendCreateRequest;
import com.poorlex.poorlex.friend.service.dto.request.FriendDenyRequest;
import com.poorlex.poorlex.friend.service.dto.request.FriendInviteRequest;
import com.poorlex.poorlex.member.domain.Member;
import com.poorlex.poorlex.member.domain.MemberNickname;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.support.IntegrationTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.token.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

class FriendControllerTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private FriendRepository friendRepository;

    @MockBean
    private MemberAlarmEventHandler memberAlarmEventHandler;

    @Test
    void 친구요청을_생성한다() throws Exception {
        //given
        final Member inviteMember = createMember("oauthId1", "nickname");
        final Member invitedMember = createMember("oauthId2", "nickname");
        final String accessToken = jwtTokenProvider.createAccessToken(inviteMember.getId());
        final FriendInviteRequest request = new FriendInviteRequest(invitedMember.getId());

        //when
        //then
        mockMvc.perform(
                post("/friends/invite")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    void 친구요청을_수락한다() throws Exception {
        //given
        final Member acceptMember = createMember("oauthId1", "nickname");
        final Member inviteMember = createMember("oauthId2", "nickname");
        final String accessToken = jwtTokenProvider.createAccessToken(acceptMember.getId());
        final FriendCreateRequest request = new FriendCreateRequest(inviteMember.getId());

        //when
        //then
        mockMvc.perform(
                post("/friends/invite/accept")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andDo(print())
            .andExpect(status().isCreated());
    }

    @Test
    void 친구요청을_거절한다() throws Exception {
        //given
        final Member denyMember = createMember("oauthId1", "nickname");
        final Member inviteMember = createMember("oauthId2", "nickname");
        final String accessToken = jwtTokenProvider.createAccessToken(denyMember.getId());
        final FriendDenyRequest request = new FriendDenyRequest(inviteMember.getId());

        //when
        //then
        mockMvc.perform(
                post("/friends/invite/deny")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    void 친구목록을_조회한다() throws Exception {
        //given
        final Member member = createMember("oauthId1", "me");
        final Member friend1 = createMember("oauthId2", "friend1");
        final Member friend2 = createMember("oauthId3", "friend2");
        final Member friend3 = createMember("oauthId4", "friend3");

        beFriend(member, friend1);
        beFriend(friend2, member);
        beFriend(member, friend3);

        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        //when
        //then
        mockMvc.perform(
                get("/friends")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            ).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0].nickname").value("friend1"))
            .andExpect(jsonPath("$[0].level").isNotEmpty())
            .andExpect(jsonPath("$[0].weeklyExpenditure").isNotEmpty())
            .andExpect(jsonPath("$[1].nickname").value("friend2"))
            .andExpect(jsonPath("$[1].level").isNotEmpty())
            .andExpect(jsonPath("$[1].weeklyExpenditure").isNotEmpty())
            .andExpect(jsonPath("$[2].nickname").value("friend3"))
            .andExpect(jsonPath("$[2].level").isNotEmpty())
            .andExpect(jsonPath("$[2].weeklyExpenditure").isNotEmpty());
    }

    private Member createMember(final String oauthId, final String nickname) {
        return memberRepository.save(Member.withoutId(oauthId, new MemberNickname(nickname)));
    }

    private void beFriend(final Member member, final Member other) {
        friendRepository.save(Friend.withoutId(member.getId(), other.getId()));
    }
}
