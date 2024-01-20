package com.poorlex.poorlex.battlenotification.service.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BattleNotificationUpdateRequest {

    private final String content;
    private final String imageUrl;
}
