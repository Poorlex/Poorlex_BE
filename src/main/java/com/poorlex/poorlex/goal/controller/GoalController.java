package com.poorlex.poorlex.goal.controller;

import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import com.poorlex.poorlex.goal.service.GoalService;
import com.poorlex.poorlex.goal.service.dto.request.GoalCreateRequest;
import com.poorlex.poorlex.goal.service.dto.response.GoalIdResponse;
import com.poorlex.poorlex.goal.service.dto.response.GoalTypeResponse;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/types")
    public ResponseEntity<List<GoalTypeResponse>> findGoalTypes() {
        final List<GoalTypeResponse> response = goalService.findAllGoalType();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<GoalIdResponse>> findMemberGoalIds(@MemberOnly final MemberInfo memberInfo) {
        final List<GoalIdResponse> response = goalService.findMemberGoalIds(memberInfo.getMemberId());
        return ResponseEntity.ok(response);
    }
}
