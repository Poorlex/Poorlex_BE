package com.poorlex.poorlex.test.controller;

import com.poorlex.poorlex.battle.battle.service.BattleService;
import com.poorlex.poorlex.battle.battle.service.dto.request.BattleFindRequest;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class BattleTestController {

    private final BattleService battleService;

    @PostMapping("/battle/{battleId}/start")
    public ResponseEntity<?> startBattle(Long battleId) {
        battleService.startBattle(battleId, LocalDateTime.now());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/battle/{battleId}/end")
    public ResponseEntity<?> endBattle(@PathVariable Long battleId) {
        battleService.endBattle(battleId, LocalDateTime.now());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/battles")
    public ResponseEntity<?> getBattles(@ParameterObject BattleFindRequest request,
                                        @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(battleService.queryBattles(request, pageable));
    }
}
