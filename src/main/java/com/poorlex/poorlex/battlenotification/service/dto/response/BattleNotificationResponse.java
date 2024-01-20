package com.poorlex.poorlex.battlenotification.service.dto.response;

import com.poorlex.poorlex.battlenotification.domain.BattleNotification;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BattleNotificationResponse {

    private final String content;
    private final String imageUrl;

    public static BattleNotificationResponse empty() {
        return new BattleNotificationResponse(null, null);
    }

    public static BattleNotificationResponse from(final BattleNotification battleNotification) {
        final Optional<String> imageUrl = battleNotification.getImageUrl();
        return imageUrl.map(url -> new BattleNotificationResponse(battleNotification.getContent(), url))
            .orElseGet(() -> new BattleNotificationResponse(battleNotification.getContent(), null));
    }
}
