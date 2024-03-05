package com.poorlex.refactoring.battle.notification.service;

import com.poorlex.refactoring.battle.notification.domain.BattleNotification;
import com.poorlex.refactoring.battle.notification.domain.BattleNotificationRepository;
import com.poorlex.refactoring.battle.notification.service.dto.response.BattleNotificationResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BattleNotificationQueryService {

    private final BattleNotificationRepository battleNotificationRepository;

    public BattleNotificationResponse findNotificationByBattleId(final Long battleId) {
        final Optional<BattleNotification> battleNotification = battleNotificationRepository.findByBattleId(battleId);

        return battleNotification.map(BattleNotificationResponse::from)
            .orElseGet(BattleNotificationResponse::empty);
    }
}
