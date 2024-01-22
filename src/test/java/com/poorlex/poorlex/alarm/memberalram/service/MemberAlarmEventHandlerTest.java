package com.poorlex.poorlex.alarm.memberalram.service;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.poorlex.poorlex.alarm.memberalram.domain.MemberAlarm;
import com.poorlex.poorlex.alarm.memberalram.domain.MemberAlarmRepository;
import com.poorlex.poorlex.alarm.memberalram.domain.MemberAlarmType;
import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleRepository;
import com.poorlex.poorlex.battle.fixture.BattleFixture;
import com.poorlex.poorlex.battleinvititation.service.event.BattleInviteAcceptedEvent;
import com.poorlex.poorlex.battleinvititation.service.event.BattleInviteDeniedEvent;
import com.poorlex.poorlex.battleinvititation.service.event.BattleInvitedEvent;
import com.poorlex.poorlex.friend.service.event.FriendAcceptedEvent;
import com.poorlex.poorlex.friend.service.event.FriendDeniedEvent;
import com.poorlex.poorlex.friend.service.event.FriendInvitedEvent;
import com.poorlex.poorlex.member.domain.Member;
import com.poorlex.poorlex.member.domain.MemberNickname;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.participate.domain.BattleParticipant;
import com.poorlex.poorlex.participate.domain.BattleParticipantRepository;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.UsingDataJpaTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberAlarmEventHandlerTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private MemberAlarmRepository memberAlarmRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private BattleParticipantRepository battleParticipantRepository;

    private MemberAlarmEventHandler memberAlarmEventHandler;

    @BeforeEach
    void setUp() {
        this.memberAlarmEventHandler = new MemberAlarmEventHandler(memberAlarmRepository);
    }

    @Test
    void 친구_초대_이벤트_발생시_초대받은_멤버의_친구_초대_알림을_생성한다() {
        //given
        final FriendInvitedEvent friendInvitedEvent = FriendInvitedEvent.builder()
            .inviteMemberId(1L)
            .invitedMemberId(2L)
            .build();

        //when
        memberAlarmEventHandler.friendInvitation(friendInvitedEvent);

        //then
        final List<MemberAlarm> memberAlarms = memberAlarmRepository.findAll();

        assertSoftly(
            softly -> {
                softly.assertThat(memberAlarms).hasSize(1);

                final MemberAlarm memberAlarm = memberAlarms.get(0);
                softly.assertThat(memberAlarm.getMemberId()).isEqualTo(friendInvitedEvent.getInvitedMemberId());
                softly.assertThat(memberAlarm.getTargetId()).isEqualTo(friendInvitedEvent.getInviteMemberId());
                softly.assertThat(memberAlarm.getType()).isEqualTo(MemberAlarmType.FRIEND_INVITATION_NOT_ACCEPTED);
            }
        );
    }

    @Test
    void 친구_수락_이벤트_발생시_초대한_멤버에게_친구_수락_알림을_생성하고_초대를_받은_멤버의_알림을_수락함으로_변경한다() {
        //given
        final Member inviteMember = createMemberWithOauthId("oauthId1", "invitor");
        final Member invitedMember = createMemberWithOauthId("oauthId2", "invited");

        createFriendInviteAlarm(inviteMember, invitedMember);

        final FriendAcceptedEvent friendAcceptedEvent = FriendAcceptedEvent.builder()
            .inviteMemberId(inviteMember.getId())
            .acceptMemberId(invitedMember.getId())
            .build();

        //when
        memberAlarmEventHandler.friendInvitationAccepted(friendAcceptedEvent);

        //then
        final Long inviteMemberId = inviteMember.getId();
        final Long invitedMemberId = invitedMember.getId();
        final List<MemberAlarm> invitedMemberAlarms = memberAlarmRepository.findAllByMemberId(invitedMemberId);
        final List<MemberAlarm> inviteMemberAlarms = memberAlarmRepository.findAllByMemberId(inviteMemberId);

        assertSoftly(
            softly -> {
                softly.assertThat(inviteMemberAlarms).hasSize(1);
                final MemberAlarm inviteMemberAlarm = inviteMemberAlarms.get(0);
                softly.assertThat(inviteMemberAlarm.getMemberId()).isEqualTo(inviteMemberId);
                softly.assertThat(inviteMemberAlarm.getTargetId()).isEqualTo(invitedMemberId);
                softly.assertThat(inviteMemberAlarm.getType()).isEqualTo(MemberAlarmType.FRIEND_ACCEPTED);

                softly.assertThat(invitedMemberAlarms).hasSize(1);
                final MemberAlarm invitedMemberAlarm = invitedMemberAlarms.get(0);
                softly.assertThat(invitedMemberAlarm.getMemberId()).isEqualTo(invitedMemberId);
                softly.assertThat(invitedMemberAlarm.getTargetId()).isEqualTo(inviteMemberId);
                softly.assertThat(invitedMemberAlarm.getType()).isEqualTo(MemberAlarmType.FRIEND_INVITATION_ACCEPTED);
            }
        );
    }

    @Test
    void 친구_거절_이벤트_발생시_초대받은_멤버의_알림을_거절로_변경한다() {
        //given
        final Member inviteMember = createMemberWithOauthId("oauthId1", "invitor");
        final Member invitedMember = createMemberWithOauthId("oauthId2", "invited");

        createFriendInviteAlarm(inviteMember, invitedMember);

        final FriendDeniedEvent friendDeniedEvent = FriendDeniedEvent.builder()
            .inviteMemberId(inviteMember.getId())
            .denyMemberId(invitedMember.getId())
            .build();

        //when
        memberAlarmEventHandler.friendInvitationDenied(friendDeniedEvent);

        //then
        final List<MemberAlarm> memberAlarms = memberAlarmRepository.findAll();

        assertSoftly(
            softly -> {
                softly.assertThat(memberAlarms).hasSize(1);

                final MemberAlarm memberAlarm = memberAlarms.get(0);
                softly.assertThat(memberAlarm.getMemberId()).isEqualTo(invitedMember.getId());
                softly.assertThat(memberAlarm.getTargetId()).isEqualTo(inviteMember.getId());
                softly.assertThat(memberAlarm.getType()).isEqualTo(MemberAlarmType.FRIEND_INVITATION_DENIED);
            }
        );
    }

    @Test
    void 배틀_초대_이벤트_발생시_초대받은_멤버의_배틀_초대_알림을_생성한다() {
        //given
        final Battle battle = battleRepository.save(BattleFixture.simple());
        final Member inviteMember = createMemberWithOauthId("oauthId1", "invitor");
        final Member invitedMember = createMemberWithOauthId("oauthId2", "invited");
        final BattleParticipant inviteBattleParticipant = join(invitedMember, battle);

        final BattleInvitedEvent battleInvitedEvent =
            new BattleInvitedEvent(inviteBattleParticipant.getId(), invitedMember.getId());

        //when
        memberAlarmEventHandler.battleInvitation(battleInvitedEvent);

        //then
        final List<MemberAlarm> memberAlarms = memberAlarmRepository.findAll();

        assertSoftly(
            softly -> {
                softly.assertThat(memberAlarms).hasSize(1);

                final MemberAlarm memberAlarm = memberAlarms.get(0);
                softly.assertThat(memberAlarm.getMemberId()).isEqualTo(invitedMember.getId());
                softly.assertThat(memberAlarm.getTargetId()).isEqualTo(inviteBattleParticipant.getId());
                softly.assertThat(memberAlarm.getType()).isEqualTo(MemberAlarmType.BATTLE_INVITATION_NOT_ACCEPTED);
            }
        );
    }

    @Test
    void 배틀_초대_수락_이벤트_발생시_초대받은_멤버의_배틀_초대_알림을_수락으로_변경한다() {
        //given
        final Battle battle = battleRepository.save(BattleFixture.simple());
        final Member inviteMember = createMemberWithOauthId("oauthId1", "invitor");
        final Member invitedMember = createMemberWithOauthId("oauthId2", "invited");
        final BattleParticipant inviteBattleParticipant = join(inviteMember, battle);
        createBattleInviteAlarm(inviteBattleParticipant, invitedMember);

        final BattleInviteAcceptedEvent battleInviteAcceptedEvent =
            new BattleInviteAcceptedEvent(
                inviteBattleParticipant.getId(),
                inviteBattleParticipant.getMemberId(),
                invitedMember.getId()
            );

        //when
        memberAlarmEventHandler.battleInvitationAccepted(battleInviteAcceptedEvent);

        //then
        final List<MemberAlarm> inviteMemberAlarms = memberAlarmRepository.findAllByMemberId(inviteMember.getId());
        final List<MemberAlarm> invitedMemberAlarms = memberAlarmRepository.findAllByMemberId(invitedMember.getId());

        assertSoftly(
            softly -> {
                softly.assertThat(inviteMemberAlarms).hasSize(1);
                final MemberAlarm inviteMemberAlarm = inviteMemberAlarms.get(0);
                softly.assertThat(inviteMemberAlarm.getMemberId()).isEqualTo(inviteBattleParticipant.getMemberId());
                softly.assertThat(inviteMemberAlarm.getTargetId()).isEqualTo(invitedMember.getId());
                softly.assertThat(inviteMemberAlarm.getType()).isEqualTo(MemberAlarmType.BATTLE_INVITATION_ACCEPT);

                softly.assertThat(invitedMemberAlarms).hasSize(1);
                final MemberAlarm invitedMemberAlarm = invitedMemberAlarms.get(0);
                softly.assertThat(invitedMemberAlarm.getMemberId()).isEqualTo(invitedMember.getId());
                softly.assertThat(invitedMemberAlarm.getTargetId()).isEqualTo(inviteBattleParticipant.getId());
                softly.assertThat(invitedMemberAlarm.getType()).isEqualTo(MemberAlarmType.BATTLE_INVITATION_ACCEPTED);
            }
        );
    }

    @Test
    void 배틀_초대_거절_이벤트_발생시_초대받은_멤버의_배틀_초대_알림을_거절으로_변경한다() {
        //given
        final Battle battle = battleRepository.save(BattleFixture.simple());
        final Member inviteMember = createMemberWithOauthId("oauthId1", "invitor");
        final Member invitedMember = createMemberWithOauthId("oauthId2", "invited");
        final BattleParticipant inviteBattleParticipant = join(invitedMember, battle);
        createBattleInviteAlarm(inviteBattleParticipant, invitedMember);

        final BattleInviteDeniedEvent battleInviteDeniedEventEvent =
            new BattleInviteDeniedEvent(
                inviteBattleParticipant.getId(),
                invitedMember.getId()
            );

        //when
        memberAlarmEventHandler.battleInvitationDenied(battleInviteDeniedEventEvent);

        //then
        final List<MemberAlarm> invitedMemberAlarms = memberAlarmRepository.findAllByMemberId(invitedMember.getId());

        assertSoftly(
            softly -> {
                softly.assertThat(invitedMemberAlarms).hasSize(1);

                final MemberAlarm invitedMemberAlarm = invitedMemberAlarms.get(0);
                softly.assertThat(invitedMemberAlarm.getMemberId()).isEqualTo(invitedMember.getId());
                softly.assertThat(invitedMemberAlarm.getTargetId()).isEqualTo(inviteBattleParticipant.getId());
                softly.assertThat(invitedMemberAlarm.getType()).isEqualTo(MemberAlarmType.BATTLE_INVITATION_DENIED);
            }
        );
    }

    private Member createMemberWithOauthId(final String oauthId, final String nickname) {
        final Member member = Member.withoutId(oauthId, new MemberNickname(nickname));
        return memberRepository.save(member);
    }

    private void createFriendInviteAlarm(final Member inviteMember, final Member invitedMember) {
        memberAlarmRepository.save(MemberAlarm.withoutId(
            invitedMember.getId(),
            inviteMember.getId(),
            MemberAlarmType.FRIEND_INVITATION_NOT_ACCEPTED)
        );
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
}
