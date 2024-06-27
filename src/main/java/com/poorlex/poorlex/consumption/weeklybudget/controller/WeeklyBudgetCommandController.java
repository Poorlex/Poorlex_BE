package com.poorlex.poorlex.consumption.weeklybudget.controller;

import com.poorlex.poorlex.consumption.weeklybudget.api.WeeklyBudgetCommandControllerSwaggerInterface;
import com.poorlex.poorlex.consumption.weeklybudget.service.WeeklyBudgetCommandService;
import com.poorlex.poorlex.consumption.weeklybudget.service.dto.request.WeeklyBudgetCreateRequest;
import com.poorlex.poorlex.security.service.MemberInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/weekly-budgets")
@RequiredArgsConstructor
public class WeeklyBudgetCommandController implements WeeklyBudgetCommandControllerSwaggerInterface {

    private final WeeklyBudgetCommandService weeklyBudgetCommandService;

    @PostMapping
    public ResponseEntity<Void> createBudget(@AuthenticationPrincipal MemberInfo memberInfo,
                                             @RequestBody WeeklyBudgetCreateRequest request) {
        weeklyBudgetCommandService.createBudgetWithCurrentDuration(memberInfo.getId(), request.getBudget());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    public ResponseEntity<Void> updateBudget(@AuthenticationPrincipal MemberInfo memberInfo,
                                             @RequestBody WeeklyBudgetCreateRequest request) {
        weeklyBudgetCommandService.updateBudget(memberInfo.getId(), request.getBudget());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteBudget(@AuthenticationPrincipal MemberInfo memberInfo) {
        weeklyBudgetCommandService.deleteBudget(memberInfo.getId());
        return ResponseEntity.noContent().build();
    }
}
