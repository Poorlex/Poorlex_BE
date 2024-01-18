package com.poolex.poolex.battlealarmreaction.service.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BattleAlarmReactionCreateRequest {

    private Long alarmId;
    private String type;
    private String content;
}
