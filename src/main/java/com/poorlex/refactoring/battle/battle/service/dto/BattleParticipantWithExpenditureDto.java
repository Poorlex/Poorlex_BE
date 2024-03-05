package com.poorlex.refactoring.battle.battle.service.dto;

import lombok.Getter;

@Getter
public class BattleParticipantWithExpenditureDto {

    private final Long memberId;
    private final String role;
    private final Long expenditure;
    private final String nickname;
    private final int level;

    public BattleParticipantWithExpenditureDto(final Long memberId,
                                               final String role,
                                               final Long expenditure,
                                               final String nickname,
                                               final int level) {
        this.memberId = memberId;
        this.role = role;
        this.expenditure = expenditure;
        this.nickname = nickname;
        this.level = level;
    }
}
