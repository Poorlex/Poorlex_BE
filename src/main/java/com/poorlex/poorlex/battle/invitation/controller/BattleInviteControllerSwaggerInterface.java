package com.poorlex.poorlex.battle.invitation.controller;

import com.poorlex.poorlex.battle.invitation.service.dto.request.BattleInviteAcceptRequest;
import com.poorlex.poorlex.battle.invitation.service.dto.request.BattleInviteDenyRequest;
import com.poorlex.poorlex.battle.invitation.service.dto.request.BattleInviteRequest;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "배틀 초대 API")
public interface BattleInviteControllerSwaggerInterface {


    @Operation(summary = "배틀 초대", description = "액세스 토큰 필요")
    @ApiResponse(responseCode = "201")
    @PostMapping("/battles/{battleId}/invite")
    public ResponseEntity<Void> invite(@Parameter(description = "초대할 배틀 ID") final Long battleId,
                                       @Parameter(hidden = true) final MemberInfo memberInfo,
                                       @Parameter(description = "초대할 회원 ID") final BattleInviteRequest request);

    @Operation(summary = "배틀 초대 수락", description = "액세스 토큰 필요")
    @ApiResponse(responseCode = "200")
    @PostMapping("/battle-invite/accept")
    public ResponseEntity<Void> inviteAccept(@Parameter(hidden = true) final MemberInfo memberInfo,
                                             @Parameter(description = "초대를 보낸 배틀참가자 ID")
                                             final BattleInviteAcceptRequest request);

    @Operation(summary = "배틀 초대 거절", description = "액세스 토큰 필요")
    @ApiResponse(responseCode = "200")
    @PostMapping("/battle-invite/deny")
    public ResponseEntity<Void> inviteAccept(@Parameter(hidden = true) final MemberInfo memberInfo,
                                             @Parameter(description = "초대를 보낸 배틀참가자 ID")
                                             final BattleInviteDenyRequest request);
}
