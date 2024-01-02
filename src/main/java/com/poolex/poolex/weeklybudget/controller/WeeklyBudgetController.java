package com.poolex.poolex.weeklybudget.controller;

import com.poolex.poolex.config.auth.argumentresolver.MemberInfo;
import com.poolex.poolex.config.auth.argumentresolver.MemberOnly;
import com.poolex.poolex.weeklybudget.service.WeeklyBudgetService;
import com.poolex.poolex.weeklybudget.service.dto.request.WeeklyBudgetCreateRequest;
import com.poolex.poolex.weeklybudget.service.dto.request.WeeklyBudgetRequest;
import com.poolex.poolex.weeklybudget.service.dto.response.WeeklyBudgetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/weekly-budgets")
@RequiredArgsConstructor
public class WeeklyBudgetController {

    private final WeeklyBudgetService weeklyBudgetService;

    @GetMapping
    public ResponseEntity<WeeklyBudgetResponse> findWeeklyBudget(@MemberOnly final MemberInfo memberInfo,
                                                                 @RequestBody final WeeklyBudgetRequest request) {
        final WeeklyBudgetResponse response = weeklyBudgetService.findCurrentBudgetByMemberIdAndDate(
            memberInfo.getMemberId(),
            request.getDate()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Void> createBudget(@MemberOnly MemberInfo memberInfo,
                                             @RequestBody WeeklyBudgetCreateRequest request) {
        weeklyBudgetService.createBudget(memberInfo.getMemberId(), request.getBudget());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
