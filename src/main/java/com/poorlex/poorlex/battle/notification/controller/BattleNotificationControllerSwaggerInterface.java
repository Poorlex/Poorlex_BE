package com.poorlex.poorlex.battle.notification.controller;

import com.poorlex.poorlex.battle.notification.service.dto.request.BattleNotificationCreateRequest;
import com.poorlex.poorlex.battle.notification.service.dto.request.BattleNotificationUpdateRequest;
import com.poorlex.poorlex.battle.notification.service.dto.response.BattleNotificationResponse;
import com.poorlex.poorlex.security.service.MemberInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "배틀 공지 API")
public interface BattleNotificationControllerSwaggerInterface {


    @Operation(summary = "배틀 공지 생성 ( 파일을 통한 이미지 업로드 작업 필요 )", description = "액세스 토큰 필요")
    @PostMapping
    @ApiResponse(responseCode = "201")
    ResponseEntity<Void> createNotification(@Parameter(description = "공지 생성할 배틀 ID") final Long battleId,
                                            @Parameter(hidden = true) final MemberInfo memberInfo,
                                            @Parameter(description = "생성할 공지 내용")
                                            final BattleNotificationCreateRequest request);

    @Operation(summary = "배틀 공지 수정 ( 파일을 통한 이미지 업로드 작업 필요 )", description = "액세스 토큰 필요")
    @PatchMapping
    @ApiResponse(responseCode = "200")
    ResponseEntity<Void> updateNotification(@Parameter(description = "공지 수정할 배틀 ID") final Long battleId,
                                            @Parameter(hidden = true) final MemberInfo memberInfo,
                                            @Parameter(description = "수정할 공지 내용") final BattleNotificationUpdateRequest request);

    @Operation(summary = "배틀 공지 조회")
    @GetMapping
    @ApiResponse(responseCode = "200")
    ResponseEntity<BattleNotificationResponse> findNotification(
            @Parameter(description = "공지를 조회할 배틀 ID") final Long battleId);
}
