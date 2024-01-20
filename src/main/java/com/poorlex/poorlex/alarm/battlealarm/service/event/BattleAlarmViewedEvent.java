package com.poorlex.poorlex.alarm.battlealarm.service.event;

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
