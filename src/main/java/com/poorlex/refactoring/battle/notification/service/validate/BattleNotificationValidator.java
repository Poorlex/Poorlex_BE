package com.poorlex.refactoring.battle.notification.service.validate;

import com.poorlex.refactoring.battle.notification.service.provider.BattleParticipantHasManagerRoleProvider;
import org.springframework.stereotype.Component;

@Component
public class BattleNotificationValidator {

    private final BattleParticipantHasManagerRoleProvider battleParticipantHasManagerRoleProvider;

    public BattleNotificationValidator(
        final BattleParticipantHasManagerRoleProvider battleParticipantHasManagerRoleProvider) {
        this.battleParticipantHasManagerRoleProvider = battleParticipantHasManagerRoleProvider;
    }

    public void hasMangerRoleByBattleIdAndMemberId(final Long battleId, final Long memberId) {
        final boolean isManager = battleParticipantHasManagerRoleProvider.byBattleIdAndMemberId(battleId, memberId);

        if (!isManager) {
            throw new BattleNotificationException.NotManagerException();
        }
    }
}
