package com.poorlex.poorlex.battlealarmreaction.controller;

import com.poorlex.poorlex.battlealarmreaction.service.AlarmReactionService;
import com.poorlex.poorlex.battlealarmreaction.service.dto.request.AlarmReactionCreateRequest;
import com.poorlex.poorlex.security.service.MemberInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<Void> createAlarmReaction(@AuthenticationPrincipal final MemberInfo memberInfo,
                                                    @RequestBody final AlarmReactionCreateRequest request) {
        alarmReactionService.createAlarmReaction(memberInfo.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
