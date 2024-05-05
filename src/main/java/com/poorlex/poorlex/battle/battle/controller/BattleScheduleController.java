package com.poorlex.poorlex.battle.battle.controller;


import com.poorlex.poorlex.batch.scheduler.BattleBatchScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/battles/schedule")
@RequiredArgsConstructor
public class BattleScheduleController implements BattleScheduleControllerSwaggerInterface {

    private final BattleBatchScheduler battleBatchScheduler;

    @PostMapping("/start")
    public ResponseEntity<Void> start() {
        battleBatchScheduler.startReadiedBattles();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/end")
    public ResponseEntity<Void> end() {
        battleBatchScheduler.giveBattlePointToParticipants();
        battleBatchScheduler.saveBattleSuccessHistoryAndEndBattle();
        return ResponseEntity.ok().build();
    }
}
