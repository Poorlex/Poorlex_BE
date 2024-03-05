package com.poorlex.refactoring.battle.notification.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BattleNotificationRepository extends JpaRepository<BattleNotification, Long> {

    Optional<BattleNotification> findByBattleId(final Long battleId);
}
