package com.poorlex.poorlex.consumption.weeklybudget.controller;

import com.poorlex.poorlex.consumption.weeklybudget.api.WeeklyBudgetQueryControllerSwaggerInterface;
import com.poorlex.poorlex.consumption.weeklybudget.service.WeeklyBudgetQueryService;
import com.poorlex.poorlex.consumption.weeklybudget.service.dto.response.WeeklyBudgetLeftResponse;
import com.poorlex.poorlex.consumption.weeklybudget.service.dto.response.WeeklyBudgetResponse;
import com.poorlex.poorlex.security.service.MemberInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/weekly-budgets")
@RequiredArgsConstructor
public class WeeklyBudgetQueryController implements WeeklyBudgetQueryControllerSwaggerInterface {

    private final WeeklyBudgetQueryService weeklyBudgetQueryService;

    @GetMapping
    public ResponseEntity<WeeklyBudgetResponse> findWeeklyBudget(@AuthenticationPrincipal final MemberInfo memberInfo) {
        return ResponseEntity.ok()
                .body(weeklyBudgetQueryService.findCurrentWeeklyBudgetByMemberId(memberInfo.getId()));
    }

    @GetMapping("/left")
    public ResponseEntity<WeeklyBudgetLeftResponse> findWeeklyBudgetLeft(@AuthenticationPrincipal final MemberInfo memberInfo) {
        return ResponseEntity.ok()
                .body(weeklyBudgetQueryService.findCurrentWeeklyBudgetLeftByMemberId(memberInfo.getId()));
    }
}
