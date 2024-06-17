package com.poorlex.poorlex.consumption.goal.controller;

import com.poorlex.poorlex.consumption.goal.service.GoalService;
import com.poorlex.poorlex.consumption.goal.service.dto.request.GoalCreateRequest;
import com.poorlex.poorlex.consumption.goal.service.dto.request.GoalUpdateRequest;
import com.poorlex.poorlex.consumption.goal.service.dto.request.MemberGoalRequest;
import com.poorlex.poorlex.consumption.goal.service.dto.response.GoalIdResponse;
import com.poorlex.poorlex.consumption.goal.service.dto.response.GoalResponse;
import com.poorlex.poorlex.consumption.goal.service.dto.response.GoalTypeResponse;
import java.net.URI;
import java.util.List;
import com.poorlex.poorlex.security.service.MemberInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ResponseEntity<Void> createGoal(@AuthenticationPrincipal final MemberInfo memberInfo,
                                           @RequestBody final GoalCreateRequest request) {
        final Long createdGoalId = goalService.createGoal(memberInfo.getId(), request);
        return ResponseEntity.created(URI.create("/goals/" + createdGoalId)).build();
    }

    @GetMapping
    public ResponseEntity<List<GoalResponse>> findMemberGoals(@AuthenticationPrincipal final MemberInfo memberInfo,
                                                              @RequestBody final MemberGoalRequest request) {
        final List<GoalResponse> response = goalService.findMemberGoalWithStatus(memberInfo.getId(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/types")
    public ResponseEntity<List<GoalTypeResponse>> findGoalTypes() {
        final List<GoalTypeResponse> response = goalService.findAllGoalType();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ids")
    public ResponseEntity<List<GoalIdResponse>> findMemberGoalIds(@AuthenticationPrincipal final MemberInfo memberInfo) {
        final List<GoalIdResponse> response = goalService.findMemberGoalIds(memberInfo.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{goalId}/finish")
    public ResponseEntity<List<GoalIdResponse>> finishGoal(@AuthenticationPrincipal final MemberInfo memberInfo,
                                                           @PathVariable(name = "goalId") final Long goalId) {
        goalService.finishGoal(memberInfo.getId(), goalId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<List<GoalIdResponse>> deleteGoal(@AuthenticationPrincipal final MemberInfo memberInfo,
                                                           @PathVariable(name = "goalId") final Long goalId) {
        goalService.deleteGoal(memberInfo.getId(), goalId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{goalId}")
    public ResponseEntity<List<GoalIdResponse>> updateGoal(@AuthenticationPrincipal final MemberInfo memberInfo,
                                                           @PathVariable(name = "goalId") final Long goalId,
                                                           @RequestBody final GoalUpdateRequest request) {
        goalService.updateGoal(memberInfo.getId(), goalId, request);
        return ResponseEntity.ok().build();
    }
}
