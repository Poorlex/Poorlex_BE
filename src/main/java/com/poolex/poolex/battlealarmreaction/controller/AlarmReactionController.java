package com.poolex.poolex.battlealarmreaction.controller;

import com.poolex.poolex.battlealarmreaction.service.AlarmReactionService;
import com.poolex.poolex.battlealarmreaction.service.dto.request.AlarmReactionCreateRequest;
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
@RequestMapping("alarm-reaction")
@RequiredArgsConstructor
public class AlarmReactionController {

    private final AlarmReactionService alarmReactionService;

    @PostMapping
    public ResponseEntity<Void> createAlarmReaction(@MemberOnly final MemberInfo memberInfo,
                                                    @RequestBody final AlarmReactionCreateRequest request) {
        alarmReactionService.createAlarmReaction(memberInfo.getMemberId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
