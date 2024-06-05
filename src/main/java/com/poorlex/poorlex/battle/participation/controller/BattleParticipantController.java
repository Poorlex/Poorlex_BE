package com.poorlex.poorlex.battle.participation.controller;

import com.poorlex.poorlex.battle.participation.service.BattleParticipantService;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import java.net.URI;

import com.poorlex.poorlex.exception.BadRequestException;
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
public class BattleParticipantController implements BattleParticipantControllerSwaggerInterface {

    private final BattleParticipantService battleParticipantService;

    @PostMapping
    public ResponseEntity<Void> participate(@PathVariable(value = "battleId") final Long battleId,
                                            @MemberOnly MemberInfo memberInfo) throws BadRequestException {
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
