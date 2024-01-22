package com.poorlex.poorlex.battleinvititation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.poorlex.poorlex.alarm.memberalram.domain.MemberAlarm;
import com.poorlex.poorlex.alarm.memberalram.domain.MemberAlarmRepository;
import com.poorlex.poorlex.alarm.memberalram.domain.MemberAlarmType;
import com.poorlex.poorlex.alarm.memberalram.service.MemberAlarmEventHandler;
import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleRepository;
import com.poorlex.poorlex.battle.domain.BattleStatus;
import com.poorlex.poorlex.battle.fixture.BattleFixture;
import com.poorlex.poorlex.battleinvititation.service.dto.request.BattleInviteAcceptRequest;
import com.poorlex.poorlex.battleinvititation.service.dto.request.BattleInviteDenyRequest;
import com.poorlex.poorlex.battleinvititation.service.dto.request.BattleInviteRequest;
import com.poorlex.poorlex.battleinvititation.service.event.BattleInviteAcceptedEvent;
import com.poorlex.poorlex.battleinvititation.service.event.BattleInviteDeniedEvent;
import com.poorlex.poorlex.battleinvititation.service.event.BattleInvitedEvent;
import com.poorlex.poorlex.member.domain.Member;
import com.poorlex.poorlex.member.domain.MemberNickname;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.participate.domain.BattleParticipant;
import com.poorlex.poorlex.participate.domain.BattleParticipantRepository;
import com.poorlex.poorlex.support.IntegrationTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@RecordApplicationEvents
class BattleInviteServiceTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @Autowired
    private ApplicationEvents events;

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private BattleParticipantRepository battleParticipantRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberAlarmRepository memberAlarmRepository;

    @Autowired
    private BattleInviteService battleInviteService;

    @MockBean
    private MemberAlarmEventHandler memberAlarmEventHandler;

    @Test
    void 배틀에_초대한다() {
        //given
        final Battle battle = createBattle();
        final Member inviteMember = createMember("oauthId1", "invitor");
        final Member invitedMember = createMember("oauthId2", "invited");
        join(inviteMember, battle);
        final BattleInviteRequest request = new BattleInviteRequest(invitedMember.getId());

        //when
        battleInviteService.invite(battle.getId(), inviteMember.getId(), request);

        //then
        final long eventCallCount = events.stream(BattleInvitedEvent.class).count();
        assertThat(eventCallCount).isOne();
    }

    @Test
    void 배틀에_초대한다_배틀참가자가_아닐떄() {
        //given
        final Battle battle = createBattle();
        final Member inviteMember = createMember("oauthId1", "invitor");
        final Member invitedMember = createMember("oauthId2", "invited");
        final BattleInviteRequest request = new BattleInviteRequest(invitedMember.getId());

        //when
        //then
        assertThatThrownBy(() -> battleInviteService.invite(battle.getId(), inviteMember.getId(), request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 배틀_초대_요청을_수락한다() {
        //given
        final Battle battle = createBattle();
        final Member inviteMember = createMember("oauthId1", "invitor");
        final Member invitedMember = createMember("oauthId2", "invited");
        final BattleParticipant inviteBattleParticipant = join(inviteMember, battle);
        createBattleInviteAlarm(inviteBattleParticipant, invitedMember);
        final BattleInviteAcceptRequest request = new BattleInviteAcceptRequest(inviteBattleParticipant.getId());

        //when
        battleInviteService.inviteAccept(invitedMember.getId(), request);

        //then
        final long eventCallCount = events.stream(BattleInviteAcceptedEvent.class).count();
        assertThat(eventCallCount).isOne();
    }

    @Test
    void 배틀_초대_요청을_수락한다_초대한_배틀참가자가_배틀방에_없을_때() {
        //given
        final Battle battle = createBattle();
        final Member inviteMember = createMember("oauthId1", "invitor");
        final Member invitedMember = createMember("oauthId2", "invited");
        final BattleParticipant inviteBattleParticipant = join(inviteMember, battle);
        createBattleInviteAlarm(inviteBattleParticipant, invitedMember);
        withdrawFromBattle(inviteBattleParticipant);
        final BattleInviteAcceptRequest request = new BattleInviteAcceptRequest(inviteBattleParticipant.getId());

        //when
        //then
        assertThatThrownBy(() -> battleInviteService.inviteAccept(invitedMember.getId(), request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("초대한 참자가가 배틀방에 없습니다.");
    }

    @Test
    void 배틀_초대_요청을_수락한다_이미_참가중인_배틀방일때() {
        //given
        final Battle battle = createBattle();
        final Member inviteMember = createMember("oauthId1", "invitor");
        final Member invitedMember = createMember("oauthId2", "invited");
        final BattleParticipant inviteBattleParticipant = join(inviteMember, battle);
        join(invitedMember, battle);
        createBattleInviteAlarm(inviteBattleParticipant, invitedMember);
        final BattleInviteAcceptRequest request = new BattleInviteAcceptRequest(inviteBattleParticipant.getId());

        //when
        //then
        assertThatThrownBy(() -> battleInviteService.inviteAccept(invitedMember.getId(), request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이미 참가중인 배틀입니다.");
    }

    @Test
    void 배틀_초대_요청을_거절한다() {
        //given
        final Battle battle = createBattle();
        final Member inviteMember = createMember("oauthId1", "invitor");
        final Member invitedMember = createMember("oauthId2", "invited");
        final BattleParticipant inviteBattleParticipant = join(inviteMember, battle);
        createBattleInviteAlarm(inviteBattleParticipant, invitedMember);
        final BattleInviteDenyRequest request = new BattleInviteDenyRequest(inviteBattleParticipant.getId());

        //when
        battleInviteService.inviteDeny(invitedMember.getId(), request);

        //then
        final long eventCallCount = events.stream(BattleInviteDeniedEvent.class).count();
        assertThat(eventCallCount).isOne();
    }

    private Member createMember(final String oauthId, final String nickname) {
        return memberRepository.save(Member.withoutId(oauthId, new MemberNickname(nickname)));
    }

    private Battle createBattle() {
        return battleRepository.save(BattleFixture.initialBattleBuilder().status(BattleStatus.PROGRESS).build());
    }

    private BattleParticipant join(final Member member, final Battle battle) {
        return battleParticipantRepository.save(BattleParticipant.normalPlayer(battle.getId(), member.getId()));
    }

    private void createBattleInviteAlarm(final BattleParticipant inviteBattleParticipant, final Member invitedMember) {
        memberAlarmRepository.save(MemberAlarm.withoutId(
            invitedMember.getId(),
            inviteBattleParticipant.getId(),
            MemberAlarmType.BATTLE_INVITATION_NOT_ACCEPTED)
        );
    }

    private void withdrawFromBattle(final BattleParticipant battleParticipant) {
        battleParticipantRepository.delete(battleParticipant);
    }
}
