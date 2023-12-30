package com.poolex.poolex.battlenotification.controller;

import com.poolex.poolex.battlenotification.service.BattleNotificationService;
import com.poolex.poolex.battlenotification.service.dto.request.BattleNotificationCreateRequest;
import com.poolex.poolex.battlenotification.service.dto.request.BattleNotificationUpdateRequest;
import com.poolex.poolex.config.auth.argumentresolver.MemberInfo;
import com.poolex.poolex.config.auth.argumentresolver.MemberOnly;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
