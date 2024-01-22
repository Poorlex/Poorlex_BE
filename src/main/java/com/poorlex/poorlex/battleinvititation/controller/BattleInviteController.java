package com.poorlex.poorlex.battleinvititation.controller;

import com.poorlex.poorlex.battleinvititation.service.BattleInviteService;
import com.poorlex.poorlex.battleinvititation.service.dto.request.BattleInviteAcceptRequest;
import com.poorlex.poorlex.battleinvititation.service.dto.request.BattleInviteDenyRequest;
import com.poorlex.poorlex.battleinvititation.service.dto.request.BattleInviteRequest;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class BattleInviteController {

    private final BattleInviteService battleInviteService;

    @PostMapping("/battles/{battleId}/invite")
    public ResponseEntity<Void> invite(@PathVariable(name = "battleId") final Long battleId,
                                       @MemberOnly final MemberInfo memberInfo,
                                       @RequestBody final BattleInviteRequest request) {
        battleInviteService.invite(battleId, memberInfo.getMemberId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/battle-invite/accept")
    public ResponseEntity<Void> inviteAccept(@MemberOnly final MemberInfo memberInfo,
                                             @RequestBody final BattleInviteAcceptRequest request) {
        battleInviteService.inviteAccept(memberInfo.getMemberId(), request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/battle-invite/deny")
    public ResponseEntity<Void> inviteAccept(@MemberOnly final MemberInfo memberInfo,
                                             @RequestBody final BattleInviteDenyRequest request) {
        battleInviteService.inviteDeny(memberInfo.getMemberId(), request);
        return ResponseEntity.ok().build();
    }
}
