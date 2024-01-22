package com.poorlex.poorlex.alarm.memberalram.service;

import com.poorlex.poorlex.alarm.memberalram.domain.MemberAlarm;
import com.poorlex.poorlex.alarm.memberalram.domain.MemberAlarmRepository;
import com.poorlex.poorlex.alarm.memberalram.domain.MemberAlarmType;
import com.poorlex.poorlex.battleinvititation.service.event.BattleInviteAcceptedEvent;
import com.poorlex.poorlex.battleinvititation.service.event.BattleInviteDeniedEvent;
import com.poorlex.poorlex.battleinvititation.service.event.BattleInvitedEvent;
import com.poorlex.poorlex.friend.service.event.FriendAcceptedEvent;
import com.poorlex.poorlex.friend.service.event.FriendDeniedEvent;
import com.poorlex.poorlex.friend.service.event.FriendInvitedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@Transactional(propagation = Propagation.REQUIRED)
@RequiredArgsConstructor
public class MemberAlarmEventHandler {

    private final MemberAlarmRepository memberAlarmRepository;

    @TransactionalEventListener(value = FriendInvitedEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void friendInvitation(final FriendInvitedEvent event) {
        final MemberAlarm memberAlarm = MemberAlarm.withoutId(
            event.getInvitedMemberId(),
            event.getInviteMemberId(),
            MemberAlarmType.FRIEND_INVITATION_NOT_ACCEPTED
        );

        memberAlarmRepository.save(memberAlarm);
    }

    @TransactionalEventListener(value = FriendAcceptedEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void friendInvitationAccepted(final FriendAcceptedEvent event) {
        updateInvitedMemberMemberAlarmToAccepted(event);
        saveInviteMemberAcceptedAlarm(event);
    }

    private void saveInviteMemberAcceptedAlarm(final FriendAcceptedEvent event) {
        final MemberAlarm memberAlarm = MemberAlarm.withoutId(
            event.getInviteMemberId(),
            event.getAcceptMemberId(),
            MemberAlarmType.FRIEND_ACCEPTED
        );
        memberAlarmRepository.save(memberAlarm);
    }

    private void updateInvitedMemberMemberAlarmToAccepted(final FriendAcceptedEvent event) {
        final MemberAlarm memberAlarm = memberAlarmRepository.findMemberAlarmByMemberIdAndTargetIdAndType(
            event.getAcceptMemberId(),
            event.getInviteMemberId(),
            MemberAlarmType.FRIEND_INVITATION_NOT_ACCEPTED
        ).orElseThrow(() -> new IllegalArgumentException("친구 요청을 받은 멤버의 초대 알림을 찾을 수 없습니다"));

        memberAlarm.updateType(MemberAlarmType.FRIEND_INVITATION_ACCEPTED);
    }

    @TransactionalEventListener(value = FriendDeniedEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void friendInvitationDenied(final FriendDeniedEvent event) {
        final MemberAlarm memberAlarm = memberAlarmRepository.findMemberAlarmByMemberIdAndTargetIdAndType(
            event.getDenyMemberId(),
            event.getInviteMemberId(),
            MemberAlarmType.FRIEND_INVITATION_NOT_ACCEPTED
        ).orElseThrow(() -> new IllegalArgumentException("친구 요청을 받은 멤버의 초대 알림을 찾을 수 없습니다"));

        memberAlarm.updateType(MemberAlarmType.FRIEND_INVITATION_DENIED);
    }

    @TransactionalEventListener(value = BattleInvitedEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void battleInvitation(final BattleInvitedEvent event) {
        final MemberAlarm memberAlarm = MemberAlarm.withoutId(
            event.getInvitedMemberId(),
            event.getInviteBattleParticipantId(),
            MemberAlarmType.BATTLE_INVITATION_NOT_ACCEPTED
        );

        memberAlarmRepository.save(memberAlarm);
    }

    @TransactionalEventListener(value = BattleInviteAcceptedEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void battleInvitationAccepted(final BattleInviteAcceptedEvent event) {
        updateInvitedMemberBattleInviteAlarm(event);
        saveInviteMemberAcceptedAlarm(event);
    }

    private void updateInvitedMemberBattleInviteAlarm(final BattleInviteAcceptedEvent event) {
        final MemberAlarm memberAlarm = memberAlarmRepository.findMemberAlarmByMemberIdAndTargetIdAndType(
            event.getInvitedMemberId(),
            event.getInviteBattleParticipantId(),
            MemberAlarmType.BATTLE_INVITATION_NOT_ACCEPTED
        ).orElseThrow(() -> new IllegalArgumentException("배틀 참가 요청을 받은 멤버의 초대 알림을 찾을 수 없습니다"));

        memberAlarm.updateType(MemberAlarmType.BATTLE_INVITATION_ACCEPTED);
    }

    private void saveInviteMemberAcceptedAlarm(final BattleInviteAcceptedEvent event) {
        final MemberAlarm memberAlarm = MemberAlarm.withoutId(
            event.getInviteMemberId(),
            event.getInvitedMemberId(),
            MemberAlarmType.BATTLE_INVITATION_ACCEPT
        );
        memberAlarmRepository.save(memberAlarm);
    }

    @TransactionalEventListener(value = BattleInviteDeniedEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void battleInvitationDenied(final BattleInviteDeniedEvent event) {
        final MemberAlarm memberAlarm = memberAlarmRepository.findMemberAlarmByMemberIdAndTargetIdAndType(
            event.getInvitedMemberId(),
            event.getInviteBattleParticipantId(),
            MemberAlarmType.BATTLE_INVITATION_NOT_ACCEPTED
        ).orElseThrow(() -> new IllegalArgumentException("배틀 참가 요청을 받은 멤버의 초대 알림을 찾을 수 없습니다"));

        memberAlarm.updateType(MemberAlarmType.BATTLE_INVITATION_DENIED);
    }
}
