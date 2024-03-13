package com.poorlex.poorlex.expenditure.api;

import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.expenditure.service.dto.request.MemberWeeklyTotalExpenditureRequest;
import com.poorlex.poorlex.expenditure.service.dto.response.BattleExpenditureResponse;
import com.poorlex.poorlex.expenditure.service.dto.response.ExpenditureResponse;
import com.poorlex.poorlex.expenditure.service.dto.response.MemberWeeklyTotalExpenditureResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "지출 관련 API")
public interface ExpenditureControllerSwaggerInterface {

    @Operation(summary = "지출 등록", description = "액세스 토큰 필요")
    @PostMapping(path = "/expenditures", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponse(responseCode = "201")
    ResponseEntity<Void> createExpenditure(
            @Parameter(hidden = true) final MemberInfo memberInfo,
            @Parameter(description = "메인 지출 이미지", required = true) final MultipartFile mainImage,
            @Parameter(description = "서브 지출 이미지", required = false) final MultipartFile subImage,
            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "지출 금액",
                    required = true
            ) final Long amount,
            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "지출 설명",
                    required = true
            ) final String description,
            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "지출 날짜(yyyy-mm-dd)",
                    required = true
            ) final LocalDate date
    );

    @Operation(
            summary = "지출 수정",
            description = """
                    액세스 토큰 필요
                                        
                    - 메인 지출 이미지 수정 방식은 다음과 같습니다.
                        - 파일만을 전달하는 경우 메인 이미지 수정을 의미합니다.
                        - URL만을 전달하는 경우 메인 이미지 변경이 없거나 이미 등록된 서브 이미지가 메인 이미지로 변경됨을 의미합니다.
                        - 파일, URL 모두 전달하지 않는 경우 메인 이미지는 반드시 존재해야 하기에 에러가 발생합니다.
                        - 파일, URL 모두 전달하는 경우는 URL만을 전달받은 것으로 처리됩니다.
                    - 서브 지출 이미지 수정 방식은 다음과 같습니다.
                        - 파일만을 전달하는 경우 서브 이미지 수정 혹은 추가를 의미합니다.
                        - URL만을 전달하는 경우 서브 이미지 변경이 없거나 이미 등록된 메인 이미지가 서브 이미지로 변경됨을 의미합니다.
                        - 파일, URL 모두 전달하지 않는 경우 원래 서브 이미지가 없거나 서브 이미지의 삭제를 의미합니다.
                        - 파일, URL 모두 전달하는 경우는 URL만을 전달받은 것으로 처리됩니다.
                    """
    )
    @PutMapping(path = "/expenditures/{expenditureId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponse(responseCode = "200")
    ResponseEntity<Void> updateExpenditure(
            @Parameter(hidden = true) final MemberInfo memberInfo,
            @Parameter(description = "수정하려는 지출 ID", required = true) final Long expenditureId,
            @Parameter(description = "메인 지출 이미지 파일", required = false) final MultipartFile mainImage,
            @Parameter(description = "메인 지출 이미지 URL", required = false) final String mainImageUrl,
            @Parameter(description = "서브 지출 이미지 파일", required = false) final MultipartFile subImage,
            @Parameter(description = "서브 지출 이미지 URL", required = false) final String subImageUrl,
            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "지출 금액",
                    required = true
            ) final Long amount,
            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "지출 설명",
                    required = true
            ) final String description
    );

    @Operation(summary = "지출 상세 조회")
    @GetMapping("/expenditures/{expenditureId}")
    @ApiResponse(responseCode = "200")
    ResponseEntity<ExpenditureResponse> findExpenditure(
            @Parameter(description = "조회하려는 지출 ID", required = true) Long expenditureId
    );

    @Operation(summary = "회원 지출 목록 조회", description = "액세스 토큰 필요")
    @GetMapping("/expenditures")
    @ApiResponse(responseCode = "200")
    ResponseEntity<List<ExpenditureResponse>> findMemberExpenditures(
            @Parameter(hidden = true) MemberInfo memberInfo
    );

    @Operation(summary = "배틀 전체 참가자 지출 목록 조회", description = "액세스 토큰 필요")
    @GetMapping(value = "/battles/{battleId}/expenditures", params = "dayOfWeek")
    @ApiResponse(responseCode = "200")
    ResponseEntity<List<BattleExpenditureResponse>> findBattleExpenditures(
            @Parameter(hidden = true) final MemberInfo memberInfo,
            @Parameter(description = "배틀 Id", required = true) final Long battleId,
            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "지출 조회할 요일 [ MONDAY |  ... | SUNDAY",
                    required = true
            ) final String dayOfWeek
    );

    @Operation(summary = "회원 특정 배틀 기간 지출 목록 조회", description = "액세스 토큰 필요")
    @GetMapping("/battles/{battleId}/expenditures")
    @ApiResponse(responseCode = "200")
    ResponseEntity<List<BattleExpenditureResponse>> findMemberBattleExpenditures(
            @Parameter(hidden = true) final MemberInfo memberInfo,
            @Parameter(description = "배틀 Id", required = true) final Long battleId
    );

    @Operation(summary = "회원 주간 총 지출 조회 ( 조회 날짜 포함 )", description = "액세스 토큰 필요")
    @GetMapping(value = "/expenditures/weekly", params = "withDate=true")
    @ApiResponse(responseCode = "200")
    ResponseEntity<MemberWeeklyTotalExpenditureResponse> findMemberWeeklyTotalExpenditures(
            @Parameter(hidden = true) final MemberInfo memberInfo,
            @Parameter(description = "조회하고자 하는 날짜", required = true) final MemberWeeklyTotalExpenditureRequest request
    );

    @Operation(summary = "회원 주간 총 지출 조회 ( 현재 날짜 기준 )", description = "액세스 토큰 필요")
    @GetMapping("/expenditures/weekly")
    @ApiResponse(responseCode = "200")
    ResponseEntity<MemberWeeklyTotalExpenditureResponse> findMemberWeeklyTotalExpenditures(
            @Parameter(hidden = true) final MemberInfo memberInfo
    );
}
