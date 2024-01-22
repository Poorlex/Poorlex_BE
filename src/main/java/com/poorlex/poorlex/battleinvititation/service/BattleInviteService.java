package com.poorlex.poorlex.battleinvititation.service;

import com.poorlex.poorlex.battleinvititation.service.dto.request.BattleInviteAcceptRequest;
import com.poorlex.poorlex.battleinvititation.service.dto.request.BattleInviteDenyRequest;
import com.poorlex.poorlex.battleinvititation.service.dto.request.BattleInviteRequest;
import com.poorlex.poorlex.battleinvititation.service.event.BattleInviteAcceptedEvent;
import com.poorlex.poorlex.battleinvititation.service.event.BattleInviteDeniedEvent;
import com.poorlex.poorlex.battleinvititation.service.event.BattleInvitedEvent;
import com.poorlex.poorlex.config.event.Events;
import com.poorlex.poorlex.friend.domain.FriendRepository;
import com.poorlex.poorlex.participate.domain.BattleParticipant;
import com.poorlex.poorlex.participate.domain.BattleParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BattleInviteService {

    private final BattleParticipantRepository battleParticipantRepository;
    private final FriendRepository friendRepository;

    @Transactional
    public void invite(final Long battleId, final Long memberId, final BattleInviteRequest request) {
        final BattleParticipant battleParticipant =
            battleParticipantRepository.findByBattleIdAndMemberId(battleId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("초대자가 배틀 참가자가 아닙니다."));
        validateIsFriend(memberId, request.getInvitedMemberId());
        Events.raise(new BattleInvitedEvent(battleParticipant.getId(), request.getInvitedMemberId()));
    }

    private void validateIsFriend(final Long firstMemberId, final Long secondMemberId) {
        final boolean firstExistCheck =
            friendRepository.existsByFirstMemberIdAndSecondMemberId(firstMemberId, secondMemberId);
        final boolean secondExistCheck =
            friendRepository.existsByFirstMemberIdAndSecondMemberId(secondMemberId, firstMemberId);
        if (!firstExistCheck && !secondExistCheck) {
            throw new IllegalArgumentException("초대한 멤버가 친구가 아닙니다.");
        }
    }

    @Transactional
    public void inviteAccept(final Long memberId, final BattleInviteAcceptRequest request) {
        final BattleParticipant battleParticipant =
            battleParticipantRepository.findById(request.getInviteBattleParticipantId())
                .orElseThrow(() -> new IllegalArgumentException("초대한 참자가가 배틀방에 없습니다."));
        validateNotParticipating(battleParticipant.getBattleId(), memberId);
        Events.raise(
            new BattleInviteAcceptedEvent(battleParticipant.getId(), battleParticipant.getMemberId(), memberId)
        );
    }

    private void validateNotParticipating(final Long battleId, final Long memberId) {
        final boolean isParticipant = battleParticipantRepository.existsByBattleIdAndMemberId(battleId, memberId);
        if (isParticipant) {
            throw new IllegalArgumentException("이미 참가중인 배틀입니다.");
        }
    }

    @Transactional
    public void inviteDeny(final Long memberId, final BattleInviteDenyRequest request) {
        Events.raise(new BattleInviteDeniedEvent(request.getInviteBattleParticipantId(), memberId));
    }
}
