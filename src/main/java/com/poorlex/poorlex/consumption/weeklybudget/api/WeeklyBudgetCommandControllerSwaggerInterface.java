package com.poorlex.poorlex.consumption.weeklybudget.api;

import com.poorlex.poorlex.consumption.weeklybudget.service.dto.request.WeeklyBudgetCreateRequest;
import com.poorlex.poorlex.security.service.MemberInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@Tag(name = "주간 예산 관리 API")
public interface WeeklyBudgetCommandControllerSwaggerInterface {

    @PostMapping("/weekly-budgets")
    @Operation(description = "주간 예산 생성")
    @ApiResponse(responseCode = "201")
    ResponseEntity<Void> createBudget(
            @Parameter(hidden = true) MemberInfo memberInfo,
            @Parameter(description = "주간 예산 요청 정보", required = true) WeeklyBudgetCreateRequest request
    );

    @PutMapping("/weekly-budgets")
    @Operation(description = "주간 예산 수정")
    @ApiResponse(responseCode = "204")
    ResponseEntity<Void> updateBudget(
            @Parameter(hidden = true) MemberInfo memberInfo,
            @Parameter(description = "주간 예산 요청 정보", required = true) WeeklyBudgetCreateRequest request
    );

    @DeleteMapping("/weekly-budgets")
    @Operation(description = "주간 예산 삭제")
    @ApiResponse(responseCode = "204")
    ResponseEntity<Void> deleteBudget(@Parameter(hidden = true) MemberInfo memberInfo);
}
