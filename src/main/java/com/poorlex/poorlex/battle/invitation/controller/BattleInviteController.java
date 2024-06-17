package com.poorlex.poorlex.battle.invitation.controller;

import com.poorlex.poorlex.battle.invitation.service.BattleInviteService;
import com.poorlex.poorlex.battle.invitation.service.dto.request.BattleInviteAcceptRequest;
import com.poorlex.poorlex.battle.invitation.service.dto.request.BattleInviteDenyRequest;
import com.poorlex.poorlex.battle.invitation.service.dto.request.BattleInviteRequest;
import com.poorlex.poorlex.security.service.MemberInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class BattleInviteController implements BattleInviteControllerSwaggerInterface {

    private final BattleInviteService battleInviteService;

    @PostMapping("/battles/{battleId}/invite")
    public ResponseEntity<Void> invite(@PathVariable(name = "battleId") final Long battleId,
                                       @AuthenticationPrincipal final MemberInfo memberInfo,
                                       @RequestBody final BattleInviteRequest request) {
        battleInviteService.invite(battleId, memberInfo.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/battle-invite/accept")
    public ResponseEntity<Void> inviteAccept(@AuthenticationPrincipal final MemberInfo memberInfo,
                                             @RequestBody final BattleInviteAcceptRequest request) {
        battleInviteService.inviteAccept(memberInfo.getId(), request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/battle-invite/deny")
    public ResponseEntity<Void> inviteAccept(@AuthenticationPrincipal final MemberInfo memberInfo,
                                             @RequestBody final BattleInviteDenyRequest request) {
        battleInviteService.inviteDeny(memberInfo.getId(), request);
        return ResponseEntity.ok().build();
    }
}
