package com.poorlex.refactoring.battle.battle.service.provider;

import com.poorlex.refactoring.battle.battle.service.dto.BattleParticipantNicknameAndLevelDto;

public interface BattleParticipantNickNameAndLevelProvider {

    BattleParticipantNicknameAndLevelDto byMemberId(final Long participantMemberId) throws IllegalArgumentException;
}
