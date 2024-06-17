package com.poorlex.poorlex.consumption.weeklybudget.api;

import com.poorlex.poorlex.consumption.weeklybudget.service.dto.request.WeeklyBudgetCreateRequest;
import com.poorlex.poorlex.security.service.MemberInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "주간 예산 관리 API")
public interface WeeklyBudgetCommandControllerSwaggerInterface {


    @PostMapping("/weekly-budgets")
    @Operation(description = "주간 예산 생성")
    @ApiResponse(responseCode = "201")
    ResponseEntity<Void> createBudget(
            @Parameter(hidden = true) MemberInfo memberInfo,
            @Parameter(description = "주간 예상 요청 정보", required = true) WeeklyBudgetCreateRequest request
    );
}
