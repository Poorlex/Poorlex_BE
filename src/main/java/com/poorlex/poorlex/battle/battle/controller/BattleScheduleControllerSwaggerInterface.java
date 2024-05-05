package com.poorlex.poorlex.battle.battle.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "[테스트용] 배틀 스케쥴링 API")
public interface BattleScheduleControllerSwaggerInterface {

    @Operation(summary = "모든 준비 배틀 시작")
    @PostMapping("/start")
    @ApiResponse(responseCode = "200")
    ResponseEntity<Void> start();

    @Operation(summary = "모든 진행 배틀 종료")
    @PostMapping("/end")
    @ApiResponse(responseCode = "200")
    ResponseEntity<Void> end();
}
