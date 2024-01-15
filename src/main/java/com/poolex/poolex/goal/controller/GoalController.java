package com.poolex.poolex.goal.controller;

import com.poolex.poolex.config.auth.argumentresolver.MemberInfo;
import com.poolex.poolex.config.auth.argumentresolver.MemberOnly;
import com.poolex.poolex.goal.service.GoalService;
import com.poolex.poolex.goal.service.dto.request.GoalCreateRequest;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    public ResponseEntity<Void> createGoal(@MemberOnly final MemberInfo memberInfo,
                                           @RequestBody final GoalCreateRequest request) {
        final Long createdGoalId = goalService.createGoal(memberInfo.getMemberId(), request);
        return ResponseEntity.created(URI.create("/goals/" + createdGoalId)).build();
    }
}
