package com.poorlex.poorlex.battleinvititation.controller;

import com.poorlex.poorlex.alarm.memberalram.domain.MemberAlarm;
import com.poorlex.poorlex.alarm.memberalram.domain.MemberAlarmRepository;
import com.poorlex.poorlex.alarm.memberalram.domain.MemberAlarmType;
import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleRepository;
import com.poorlex.poorlex.battle.domain.BattleStatus;
import com.poorlex.poorlex.battle.fixture.BattleFixture;
import com.poorlex.poorlex.battleinvititation.service.dto.request.BattleInviteAcceptRequest;
import com.poorlex.poorlex.battleinvititation.service.dto.request.BattleInviteDenyRequest;
import com.poorlex.poorlex.battleinvititation.service.dto.request.BattleInviteRequest;
import com.poorlex.poorlex.friend.domain.Friend;
import com.poorlex.poorlex.friend.domain.FriendRepository;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberNickname;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId;
import com.poorlex.poorlex.participate.domain.BattleParticipant;
import com.poorlex.poorlex.participate.domain.BattleParticipantRepository;
import com.poorlex.poorlex.support.IntegrationTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.token.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("배틀 초대 인수 테스트")
class BattleInviteControllerTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private BattleParticipantRepository battleParticipantRepository;

    @Autowired
    private MemberAlarmRepository memberAlarmRepository;

    @Test
    void 배틀에_초대한다() throws Exception {
        //given
        final Battle battle = createBattle();
        final Member inviteMember = createMember("oauthId1", "invitor");
        final Member invitedMember = createMember("oauthId2", "invited");
        beFriend(inviteMember, invitedMember);
        join(inviteMember, battle);

        final String accessToken = jwtTokenProvider.createAccessToken(inviteMember.getId());
        final BattleInviteRequest request = new BattleInviteRequest(invitedMember.getId());

        //when
        //then
        mockMvc.perform(
                        post("/battles/{battleId}/invite", battle.getId())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void 배틀_초대_요청을_수락한다() throws Exception {
        //given
        final Battle battle = createBattle();
        final Member inviteMember = createMember("oauthId1", "invitor");
        final Member invitedMember = createMember("oauthId2", "invited");
        final BattleParticipant inviteBattleParticipant = join(inviteMember, battle);
        createBattleInviteAlarm(inviteBattleParticipant, invitedMember);

        final String accessToken = jwtTokenProvider.createAccessToken(invitedMember.getId());
        final BattleInviteAcceptRequest request = new BattleInviteAcceptRequest(inviteBattleParticipant.getId());

        //when
        //then
        mockMvc.perform(
                        post("/battle-invite/accept")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void 배틀_초대_요청을_거절한다() throws Exception {
        //given
        final Battle battle = createBattle();
        final Member inviteMember = createMember("oauthId1", "invitor");
        final Member invitedMember = createMember("oauthId2", "invited");
        final BattleParticipant inviteBattleParticipant = join(inviteMember, battle);
        createBattleInviteAlarm(inviteBattleParticipant, invitedMember);

        final String accessToken = jwtTokenProvider.createAccessToken(invitedMember.getId());
        final BattleInviteDenyRequest request = new BattleInviteDenyRequest(inviteBattleParticipant.getId());

        //when
        //then
        mockMvc.perform(
                        post("/battle-invite/deny")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());
    }

    private Member createMember(final String oauthId, final String nickname) {
        return memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, oauthId, new MemberNickname(nickname)));
    }

    private Battle createBattle() {
        return battleRepository.save(BattleFixture.initialBattleBuilder().status(BattleStatus.PROGRESS).build());
    }

    private BattleParticipant join(final Member member, final Battle battle) {
        return battleParticipantRepository.save(BattleParticipant.normalPlayer(battle.getId(), member.getId()));
    }

    private void beFriend(final Member member, final Member other) {
        friendRepository.save(Friend.withoutId(member.getId(), other.getId()));
    }

    private void createBattleInviteAlarm(final BattleParticipant inviteBattleParticipant, final Member invitedMember) {
        memberAlarmRepository.save(MemberAlarm.withoutId(
                invitedMember.getId(),
                inviteBattleParticipant.getId(),
                MemberAlarmType.BATTLE_INVITATION_NOT_ACCEPTED)
        );
    }
}
