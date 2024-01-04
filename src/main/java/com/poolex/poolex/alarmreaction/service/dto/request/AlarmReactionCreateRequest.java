package com.poolex.poolex.alarmreaction.service.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AlarmReactionCreateRequest {

    private Long alarmId;
    private String type;
    private String content;
}
