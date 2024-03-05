package com.poorlex.refactoring.battle.invitation.service;

import com.poorlex.refactoring.battle.invitation.service.dto.request.BattleInviteAcceptRequest;
import com.poorlex.refactoring.battle.invitation.service.dto.request.BattleInviteDenyRequest;
import com.poorlex.refactoring.battle.invitation.service.dto.request.BattleInviteRequest;
import com.poorlex.refactoring.battle.invitation.service.event.BattleInviteAcceptedEvent;
import com.poorlex.refactoring.battle.invitation.service.event.BattleInviteDeniedEvent;
import com.poorlex.refactoring.battle.invitation.service.event.BattleInvitedEvent;
import com.poorlex.refactoring.battle.invitation.service.provider.BattleIdProvider;
import com.poorlex.refactoring.battle.invitation.service.provider.BattleParticipantIdProvider;
import com.poorlex.refactoring.battle.invitation.service.provider.MemberIdProvider;
import com.poorlex.refactoring.battle.invitation.service.validate.BattleInviteValidator;
import com.poorlex.refactoring.config.event.Events;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BattleInviteCommandService {

    private final BattleInviteValidator validator;
    private final BattleIdProvider battleIdProvider;
    private final MemberIdProvider memberIdProvider;
    private final BattleParticipantIdProvider battleParticipantIdProvider;

    public void invite(final Long battleId, final Long memberId, final BattleInviteRequest request) {
        validator.participating(battleId, memberId);
        validator.isFriend(memberId, request.getInvitedMemberId());
        final Long battleParticipantId = battleParticipantIdProvider.byBattleIdAndMemberId(battleId, memberId);

        Events.raise(new BattleInvitedEvent(battleParticipantId, request.getInvitedMemberId()));
    }

    public void inviteAccept(final Long memberId, final BattleInviteAcceptRequest request) {
        final Long invitorBattleParticipantId = request.getInvitorBattleParticipantId();

        validator.participating(invitorBattleParticipantId);
        final Long invitedBattleId = battleIdProvider.byParticipantId(invitorBattleParticipantId);
        validator.notParticipating(memberId, invitedBattleId);

        final Long invitorMemberId = memberIdProvider.byParticipantId(invitorBattleParticipantId);
        Events.raise(
            new BattleInviteAcceptedEvent(invitorBattleParticipantId, invitorMemberId, memberId)
        );
    }

    public void inviteDeny(final Long memberId, final BattleInviteDenyRequest request) {
        Events.raise(new BattleInviteDeniedEvent(request.getInvitorBattleParticipantId(), memberId));
    }
}
