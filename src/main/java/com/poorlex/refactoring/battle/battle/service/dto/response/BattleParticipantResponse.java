package com.poorlex.refactoring.battle.battle.service.dto.response;

import com.poorlex.refactoring.battle.battle.service.dto.BattleParticipantWithExpenditureDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BattleParticipantResponse {

    private final Long memberId;
    private final int rank;
    private final int level;
    private final String role;
    private final String nickname;
    private final Long expenditure;

    public static BattleParticipantResponse from(final BattleParticipantWithExpenditureDto battleParticipantDto,
                                                 final int rank) {
        return new BattleParticipantResponse(
            battleParticipantDto.getMemberId(),
            rank,
            battleParticipantDto.getLevel(),
            battleParticipantDto.getRole(),
            battleParticipantDto.getNickname(),
            battleParticipantDto.getExpenditure()
        );
    }
}
