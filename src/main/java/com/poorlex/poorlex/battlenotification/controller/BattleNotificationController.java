package com.poorlex.poorlex.battlenotification.controller;

import com.poorlex.poorlex.battlenotification.service.BattleNotificationService;
import com.poorlex.poorlex.battlenotification.service.dto.request.BattleNotificationCreateRequest;
import com.poorlex.poorlex.battlenotification.service.dto.request.BattleNotificationUpdateRequest;
import com.poorlex.poorlex.battlenotification.service.dto.response.BattleNotificationResponse;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/battles/{battleId}/notification")
@RequiredArgsConstructor
public class BattleNotificationController {

    private final BattleNotificationService battleNotificationService;

    @PostMapping
    public ResponseEntity<Void> createNotification(@PathVariable(name = "battleId") final Long battleId,
                                                   @MemberOnly final MemberInfo memberInfo,
                                                   @RequestBody final BattleNotificationCreateRequest request) {
        battleNotificationService.createNotification(battleId, memberInfo.getMemberId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    public ResponseEntity<Void> updateNotification(@PathVariable(name = "battleId") final Long battleId,
                                                   @MemberOnly final MemberInfo memberInfo,
                                                   @RequestBody final BattleNotificationUpdateRequest request) {
        battleNotificationService.updateNotification(battleId, memberInfo.getMemberId(), request);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<BattleNotificationResponse> findNotification(
        @PathVariable(name = "battleId") final Long battleId) {
        final BattleNotificationResponse response = battleNotificationService.findNotificationByBattleId(battleId);

        return ResponseEntity.ok(response);
    }
}
