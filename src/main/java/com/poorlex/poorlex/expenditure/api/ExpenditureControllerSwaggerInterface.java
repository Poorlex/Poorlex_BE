package com.poorlex.poorlex.expenditure.api;

import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.expenditure.service.dto.request.ExpenditureCreateRequest;
import com.poorlex.poorlex.expenditure.service.dto.request.MemberWeeklyTotalExpenditureRequest;
import com.poorlex.poorlex.expenditure.service.dto.response.BattleExpenditureResponse;
import com.poorlex.poorlex.expenditure.service.dto.response.ExpenditureResponse;
import com.poorlex.poorlex.expenditure.service.dto.response.MemberWeeklyTotalExpenditureResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "지출 관련 API")
public interface ExpenditureControllerSwaggerInterface {

    @Operation(summary = "지출 등록", description = "액세스 토큰 필요")
    @PostMapping(path = "/expenditures", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponse(responseCode = "201")
    ResponseEntity<Void> createExpenditure(
        @Parameter(hidden = true) MemberInfo memberInfo,
        @Parameter(description = "지출 이미지 (1개 ~ 2개)") List<MultipartFile> images,
        @Parameter(
            description = "등록할 지출 정보",
            content = @Content(encoding = @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE))
        ) ExpenditureCreateRequest request
    );

    @Operation(summary = "지출 상세 조회")
    @GetMapping("/expenditures/{expenditureId}")
    @ApiResponse(responseCode = "200")
    ResponseEntity<ExpenditureResponse> findExpenditure(
        @Parameter(description = "조회하려는 지출 ID") Long expenditureId);

    @Operation(summary = "회원 지출 목록 조회", description = "액세스 토큰 필요")
    @GetMapping("/expenditures")
    @ApiResponse(responseCode = "200")
    ResponseEntity<List<ExpenditureResponse>> findMemberExpenditures(
        @Parameter(hidden = true) MemberInfo memberInfo);

    @Operation(summary = "배틀 전체 참가자 지출 목록 조회", description = "액세스 토큰 필요")
    @GetMapping(value = "/battles/{battleId}/expenditures", params = "dayOfWeek")
    @ApiResponse(responseCode = "200")
    ResponseEntity<List<BattleExpenditureResponse>> findBattleExpenditures(
        @Parameter(hidden = true) final MemberInfo memberInfo,
        @Parameter(description = "배틀 Id") final Long battleId,
        @Parameter(description = "지출 조회할 요일 [ MONDAY |  ... | SUNDAY") final String dayOfWeek
    );

    @Operation(summary = "회원 특정 배틀 기간 지출 목록 조회", description = "액세스 토큰 필요")
    @GetMapping("/battles/{battleId}/expenditures")
    @ApiResponse(responseCode = "200")
    ResponseEntity<List<BattleExpenditureResponse>> findMemberBattleExpenditures(
        @Parameter(hidden = true) final MemberInfo memberInfo,
        @Parameter(description = "배틀 Id") final Long battleId
    );

    @Operation(summary = "회원 주간 총 지출 조회", description = "액세스 토큰 필요")
    @GetMapping("/expenditures/weekly")
    @ApiResponse(responseCode = "200")
    ResponseEntity<MemberWeeklyTotalExpenditureResponse> findMemberWeeklyTotalExpenditures(
        @Parameter(hidden = true) final MemberInfo memberInfo,
        @Parameter(description = "조회 기준 날짜") final MemberWeeklyTotalExpenditureRequest request);
}
