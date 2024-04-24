package com.poorlex.poorlex.battle.participation.controller;

import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import com.poorlex.poorlex.battle.participation.service.BattleParticipantService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/battles/{battleId}/participants")
@RequiredArgsConstructor
public class BattleParticipantController {

    private final BattleParticipantService battleParticipantService;

    @PostMapping
    public ResponseEntity<Void> participate(@PathVariable(value = "battleId") final Long battleId,
                                            @MemberOnly MemberInfo memberInfo) {
        final Long battleParticipantId = battleParticipantService.participate(battleId, memberInfo.getMemberId());
        final String locationHeader = "/battles/" + battleId + "/participants/" + battleParticipantId;

        return ResponseEntity.created(URI.create(locationHeader)).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> withdraw(@PathVariable(value = "battleId") final Long battleId,
                                         @MemberOnly MemberInfo memberInfo) {
        battleParticipantService.withdraw(battleId, memberInfo.getMemberId());
        return ResponseEntity.noContent().build();
    }
}
