package com.poolex.poolex.participate.controller;

import com.poolex.poolex.config.auth.argumentresolver.MemberInfo;
import com.poolex.poolex.config.auth.argumentresolver.MemberOnly;
import com.poolex.poolex.participate.service.BattleParticipantService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Void> createParticipant(@PathVariable(value = "battleId") final Long battleId,
                                                  @MemberOnly MemberInfo memberInfo) {
        final Long battleParticipantId = battleParticipantService.create(battleId, memberInfo.getMemberId());
        final String locationHeader = "/battles/" + battleId + "/participants/" + battleParticipantId;
        
        return ResponseEntity.created(URI.create(locationHeader)).build();
    }
}
