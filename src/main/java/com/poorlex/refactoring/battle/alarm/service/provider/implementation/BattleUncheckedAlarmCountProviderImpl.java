package com.poorlex.refactoring.battle.alarm.service.provider.implementation;

import com.poorlex.refactoring.battle.alarm.service.BattleAlarmQueryService;
import com.poorlex.refactoring.battle.battle.service.provider.BattleUncheckedAlarmCountProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BattleUncheckedAlarmCountProviderImpl implements BattleUncheckedAlarmCountProvider {

    private final BattleAlarmQueryService battleAlarmQueryService;

    @Override
    public int getByBattleIdAndMemberId(final Long battleId, final Long memberId) {
        return battleAlarmQueryService.getBattleParticipantUncheckedAlarmCount(battleId, memberId);
    }
}
