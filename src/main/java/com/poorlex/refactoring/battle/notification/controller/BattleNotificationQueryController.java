package com.poorlex.refactoring.battle.notification.controller;

import com.poorlex.refactoring.battle.notification.service.BattleNotificationQueryService;
import com.poorlex.refactoring.battle.notification.service.dto.response.BattleNotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/battles/{battleId}/notification")
@RequiredArgsConstructor
public class BattleNotificationQueryController {

    private final BattleNotificationQueryService battleNotificationQueryService;

    @GetMapping
    public ResponseEntity<BattleNotificationResponse> findNotification(
        @PathVariable(name = "battleId") final Long battleId) {
        return ResponseEntity.ok(battleNotificationQueryService.findNotificationByBattleId(battleId));
    }
}
