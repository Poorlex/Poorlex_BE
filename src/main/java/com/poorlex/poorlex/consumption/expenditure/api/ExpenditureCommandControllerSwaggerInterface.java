package com.poorlex.poorlex.consumption.expenditure.api;

import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "지출 관리 API")
public interface ExpenditureCommandControllerSwaggerInterface {

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

    @Operation(summary = "지출 삭제", description = "액세스 토큰 필요")
    @DeleteMapping("/expenditures/{expenditureId}")
    @ApiResponse(responseCode = "200")
    public ResponseEntity<Void> deleteExpenditure(
            @Parameter(hidden = true) final MemberInfo memberInfo,
            @Parameter(in = ParameterIn.PATH, description = "지출 Id", required = true) final Long expenditureId
    );
}
