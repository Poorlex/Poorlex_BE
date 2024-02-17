package com.poorlex.poorlex.alarm.battlealarm.api;

import com.poorlex.poorlex.alarm.battlealarm.service.dto.request.BattleAlarmRequest;
import com.poorlex.poorlex.alarm.battlealarm.service.dto.response.AbstractBattleAlarmResponse;
import com.poorlex.poorlex.alarm.battlealarm.service.dto.response.BattleAlarmResponse;
import com.poorlex.poorlex.alarm.battlealarm.service.dto.response.UncheckedBattleAlarmCountResponse;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.voting.vote.service.dto.response.VoteResponse;
import com.poorlex.poorlex.voting.votingpaper.service.dto.response.VotingPaperResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "배틀 알림 관련 API")
public interface BattleAlarmControllerSwaggerInterface {

    @Operation(summary = "배틀 알림 조회", description = "액세스 토큰 필요")
    @GetMapping("/battles/{battleId}/alarms")
    @ApiResponse(responseCode = "200", description = "아래 형식의 응답들을 가진 리스트 ( 공통 필드 : alarmType, createdAt)")
    @ApiResponse(responseCode = "배틀 알림", content = @Content(schema = @Schema(implementation = BattleAlarmResponse.class)))
    @ApiResponse(responseCode = "투표 알림", content = @Content(schema = @Schema(implementation = VoteResponse.class)))
    @ApiResponse(responseCode = "투표 표 알림", content = @Content(schema = @Schema(implementation = VotingPaperResponse.class)))
    ResponseEntity<List<AbstractBattleAlarmResponse>> findBattleAlarms(
        @Parameter(hidden = true) final MemberInfo memberInfo,
        @Parameter(description = "배틀 Id") final Long battleId,
        @Parameter(description = "배틀 알림 조회 시간") final BattleAlarmRequest request);

    @Operation(summary = "미확인 배틀 알림 수 조회", description = "액세스 토큰 필요")
    @GetMapping("/battles/{battleId}/alarms/unchecked")
    @ApiResponse(responseCode = "200")
    ResponseEntity<UncheckedBattleAlarmCountResponse> getUncheckedBattleAlarmCount(
        @Parameter(hidden = true) final MemberInfo memberInfo,
        @Parameter(description = "배틀 Id") final Long battleId
    );

}
