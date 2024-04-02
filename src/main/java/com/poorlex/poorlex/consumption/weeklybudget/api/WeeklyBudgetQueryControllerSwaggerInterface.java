package com.poorlex.poorlex.consumption.weeklybudget.api;

import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.consumption.weeklybudget.service.dto.response.WeeklyBudgetLeftResponse;
import com.poorlex.poorlex.consumption.weeklybudget.service.dto.response.WeeklyBudgetResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "주간 예산 조회 API")
public interface WeeklyBudgetQueryControllerSwaggerInterface {

    @GetMapping("/weekly-budgets")
    @Operation(description = "요청 날짜 포함 주간 예산 조회")
    @ApiResponse(responseCode = "200")
    ResponseEntity<WeeklyBudgetResponse> findWeeklyBudget(@Parameter(hidden = true) MemberInfo memberInfo);

    @GetMapping("/weekly-budgets/left")
    @Operation(description = "요청 날짜 포함 주간 예산 남은 금액 조회")
    @ApiResponse(responseCode = "200")
    ResponseEntity<WeeklyBudgetLeftResponse> findWeeklyBudgetLeft(@Parameter(hidden = true) MemberInfo memberInfo);
}
