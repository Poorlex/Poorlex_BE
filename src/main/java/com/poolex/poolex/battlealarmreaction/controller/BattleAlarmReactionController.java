package com.poolex.poolex.battlealarmreaction.controller;

import com.poolex.poolex.battlealarmreaction.service.BattleAlarmReactionService;
import com.poolex.poolex.battlealarmreaction.service.dto.request.BattleAlarmReactionCreateRequest;
import com.poolex.poolex.config.auth.argumentresolver.MemberInfo;
import com.poolex.poolex.config.auth.argumentresolver.MemberOnly;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("battle-alarm-reaction")
@RequiredArgsConstructor
public class BattleAlarmReactionController {

    private final BattleAlarmReactionService battleAlarmReactionService;

    @PostMapping
    public ResponseEntity<Void> createAlarmReaction(@MemberOnly final MemberInfo memberInfo,
                                                    @RequestBody final BattleAlarmReactionCreateRequest request) {
        battleAlarmReactionService.createAlarmReaction(memberInfo.getMemberId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
