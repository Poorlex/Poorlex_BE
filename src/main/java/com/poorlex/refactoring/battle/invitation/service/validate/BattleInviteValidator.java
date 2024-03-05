package com.poorlex.refactoring.battle.invitation.service.validate;

import com.poorlex.refactoring.battle.invitation.service.provider.BattleParticipantExistenceProvider;
import com.poorlex.refactoring.battle.invitation.service.provider.FriendExistenceProvider;
import org.springframework.stereotype.Component;

@Component
public class BattleInviteValidator {

    private final FriendExistenceProvider friendExistenceProvider;
    private final BattleParticipantExistenceProvider battleParticipantExistenceProvider;

    public BattleInviteValidator(final FriendExistenceProvider friendExistenceProvider,
                                 final BattleParticipantExistenceProvider battleParticipantExistenceProvider) {
        this.friendExistenceProvider = friendExistenceProvider;
        this.battleParticipantExistenceProvider = battleParticipantExistenceProvider;
    }

    public void isFriend(final Long firstMemberId, final Long secondMemberId) {
        final boolean exitFriendship = friendExistenceProvider.isExist(firstMemberId, secondMemberId);
        if (!exitFriendship) {
            throw new BattleInviteException.InviteOnlyByFriendException();
        }
    }

    public void participating(final Long battleId, final Long memberId) {
        final boolean isParticipating = battleParticipantExistenceProvider.isExistByBattleIdAndMemberId(battleId,
            memberId);
        if (isParticipating) {
            throw new BattleInviteException.AlreadyParticipatingBattleException();
        }
    }

    public void participating(final Long battleParticipantId) {
        final boolean isParticipating = battleParticipantExistenceProvider.isExistByBattleParticipantId(
            battleParticipantId);
        if (isParticipating) {
            throw new BattleInviteException.AlreadyParticipatingBattleException();
        }
    }

    public void notParticipating(final Long memberId, final Long battleId) {
        final boolean isParticipating = battleParticipantExistenceProvider.isExistByBattleIdAndMemberId(battleId,
            memberId);
        if (isParticipating) {
            throw new BattleInviteException.NotParticipatingBattleException();
        }
    }
}
