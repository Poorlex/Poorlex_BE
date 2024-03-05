package com.poorlex.refactoring.battle.participant.controller;

import com.poorlex.refactoring.battle.participant.service.BattleParticipantCommandService;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/battles/{battleId}/participants")
public class BattleParticipantCommandController {

    private final BattleParticipantCommandService battleParticipantCommandService;

    public BattleParticipantCommandController(final BattleParticipantCommandService battleParticipantCommandService) {
        this.battleParticipantCommandService = battleParticipantCommandService;
    }

    @PostMapping
    public ResponseEntity<Void> participate(@PathVariable(value = "battleId") final Long battleId,
                                            @MemberOnly MemberInfo memberInfo) {
        final Long battleParticipantId = battleParticipantCommandService.participate(battleId,
            memberInfo.getMemberId());
        final String locationHeader = "/battles/" + battleId + "/participants/" + battleParticipantId;

        return ResponseEntity.created(URI.create(locationHeader)).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> withdraw(@PathVariable(value = "battleId") final Long battleId,
                                         @MemberOnly MemberInfo memberInfo) {
        battleParticipantCommandService.withdraw(battleId, memberInfo.getMemberId());
        return ResponseEntity.noContent().build();
    }
}
