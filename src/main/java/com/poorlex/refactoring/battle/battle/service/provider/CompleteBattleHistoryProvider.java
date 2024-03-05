package com.poorlex.refactoring.battle.battle.service.provider;

import com.poorlex.refactoring.battle.battle.service.dto.BattleHistoryDto;
import java.util.List;

public interface CompleteBattleHistoryProvider {

    List<BattleHistoryDto> getByMemberId(Long memberId);
}
