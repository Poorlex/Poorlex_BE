package com.poorlex.refactoring.battle.battle.domain;

import java.time.LocalDateTime;

public class BattleFactory {

    private BattleFactory() {

    }

    public static Battle create(final Long id,
                                final String name,
                                final String introduction,
                                final String imageUrl,
                                final Long budget,
                                final int maxParticipantSize,
                                final LocalDateTime start,
                                final LocalDateTime end,
                                final BattleStatus status) {
        return new Battle(
            id,
            BattleCreateValidator.validBattleName(name),
            BattleCreateValidator.validBattleIntroduction(introduction),
            BattleCreateValidator.validBattleImageUrl(imageUrl),
            BattleCreateValidator.validBattleBudget(budget),
            BattleCreateValidator.validParticipantSize(maxParticipantSize),
            BattleCreateValidator.validBattleDuration(start, end),
            status,
            false
        );
    }

    public static Battle createWithoutId(final String name,
                                         final String introduction,
                                         final String imageUrl,
                                         final Long budget,
                                         final int maxParticipantSize,
                                         final LocalDateTime start,
                                         final LocalDateTime end,
                                         final BattleStatus status) {
        return new Battle(
            null,
            BattleCreateValidator.validBattleName(name),
            BattleCreateValidator.validBattleIntroduction(introduction),
            BattleCreateValidator.validBattleImageUrl(imageUrl),
            BattleCreateValidator.validBattleBudget(budget),
            BattleCreateValidator.validParticipantSize(maxParticipantSize),
            BattleCreateValidator.validBattleDuration(start, end),
            status,
            false
        );
    }
}
