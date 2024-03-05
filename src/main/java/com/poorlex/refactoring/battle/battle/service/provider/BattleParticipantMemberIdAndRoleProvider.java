package com.poorlex.refactoring.battle.battle.service.provider;

import com.poorlex.refactoring.battle.battle.service.dto.BattleParticipantDto;
import java.util.List;

public interface BattleParticipantMemberIdAndRoleProvider {

    List<BattleParticipantDto> getByBattleId(final Long battleId);
}
