package com.poorlex.refactoring.battle.alarm.service.event;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BattleAlarmViewedEvent {

    private final Long battleId;
    private final Long memberId;
    private final LocalDateTime viewTime;
}
