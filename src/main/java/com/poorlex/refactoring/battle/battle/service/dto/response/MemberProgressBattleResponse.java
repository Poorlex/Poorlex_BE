package com.poorlex.refactoring.battle.battle.service.dto.response;

import com.poorlex.refactoring.battle.battle.domain.Battle;

public class MemberProgressBattleResponse extends BattleAndCurrentParticipantSizeResponse {

    private final long dDay;
    private final int uncheckedAlarmCount;

    public MemberProgressBattleResponse(final Battle battle,
                                        final long dDay,
                                        final int currentParticipantSize,
                                        final int uncheckedAlarmCount) {
        super(battle, currentParticipantSize);
        this.dDay = dDay;
        this.uncheckedAlarmCount = uncheckedAlarmCount;
    }

    public long getDDay() {
        return dDay;
    }

    public int getUncheckedAlarmCount() {
        return uncheckedAlarmCount;
    }
}
