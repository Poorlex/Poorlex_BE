package com.poorlex.poorlex.battle.participation.controller;

import com.poorlex.poorlex.battle.participation.service.BattleParticipantService;
import java.net.URI;
import com.poorlex.poorlex.security.service.MemberInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/battles/{battleId}/participants")
@RequiredArgsConstructor
public class BattleParticipantController implements BattleParticipantControllerSwaggerInterface {

    private final BattleParticipantService battleParticipantService;

    @PostMapping
    public ResponseEntity<Void> participate(@PathVariable(value = "battleId") final Long battleId,
                                            @AuthenticationPrincipal MemberInfo memberInfo) {
        final Long battleParticipantId = battleParticipantService.participate(battleId, memberInfo.getId());
        final String locationHeader = "/battles/" + battleId + "/participants/" + battleParticipantId;

        return ResponseEntity.created(URI.create(locationHeader)).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> withdraw(@PathVariable(value = "battleId") final Long battleId,
                                         @AuthenticationPrincipal MemberInfo memberInfo) {
        battleParticipantService.withdraw(battleId, memberInfo.getId());
        return ResponseEntity.noContent().build();
    }
}
