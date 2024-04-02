package com.poorlex.poorlex.consumption.weeklybudget.controller;

import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import com.poorlex.poorlex.consumption.weeklybudget.api.WeeklyBudgetCommandControllerSwaggerInterface;
import com.poorlex.poorlex.consumption.weeklybudget.service.WeeklyBudgetCommandService;
import com.poorlex.poorlex.consumption.weeklybudget.service.dto.request.WeeklyBudgetCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/weekly-budgets")
@RequiredArgsConstructor
public class WeeklyBudgetCommandController implements WeeklyBudgetCommandControllerSwaggerInterface {

    private final WeeklyBudgetCommandService weeklyBudgetCommandService;

    @PostMapping
    public ResponseEntity<Void> createBudget(@MemberOnly MemberInfo memberInfo,
                                             @RequestBody WeeklyBudgetCreateRequest request) {
        weeklyBudgetCommandService.createBudgetWithCurrentDuration(memberInfo.getMemberId(), request.getBudget());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
