package com.poorlex.refactoring.battle.battle.controller;

import com.poorlex.refactoring.battle.battle.service.BattleCommandService;
import com.poorlex.refactoring.battle.battle.service.dto.request.BattleCreateRequest;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/battles")
public class BattleCommandController {

    private final static String PATH_DELIMITER = "/";
    private final BattleCommandService battleCommandService;

    public BattleCommandController(final BattleCommandService battleCommandService) {
        this.battleCommandService = battleCommandService;
    }

    @PostMapping
    public ResponseEntity<Void> createBattle(@MemberOnly final MemberInfo memberInfo,
                                             @RequestBody final BattleCreateRequest request) {
        final Long createdBattleId = battleCommandService.create(memberInfo.getMemberId(), request);
        final String controllerPath = getClass().getDeclaredAnnotation(RequestMapping.class).path()[0];

        return ResponseEntity.created(URI.create(controllerPath + PATH_DELIMITER + createdBattleId)).build();
    }
}
