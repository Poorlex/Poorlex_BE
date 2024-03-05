package com.poorlex.refactoring.expenditure.controller;

import com.poorlex.refactoring.expenditure.service.BattleExpenditureQueryService;
import com.poorlex.refactoring.expenditure.service.dto.response.BattleExpenditureResponse;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/battles/{battleId}/expenditures")
@RequiredArgsConstructor
public class BattleExpenditureQueryController {

    private final BattleExpenditureQueryService expenditureService;

    @GetMapping()
    public ResponseEntity<List<BattleExpenditureResponse>> findMemberBattleExpenditures(
        @MemberOnly final MemberInfo memberInfo,
        @PathVariable(name = "battleId") final Long battleId
    ) {
        final List<BattleExpenditureResponse> responses = expenditureService.findMemberBattleExpenditures(
            battleId,
            memberInfo.getMemberId()
        );

        return ResponseEntity.ok(responses);
    }

    @GetMapping(params = "dayOfWeek")
    public ResponseEntity<List<BattleExpenditureResponse>> findBattleExpenditures(
        @MemberOnly final MemberInfo memberInfo,
        @PathVariable(name = "battleId") final Long battleId,
        @RequestParam(name = "dayOfWeek") final String dayOfWeek
    ) {
        return ResponseEntity.ok()
            .body(expenditureService.findBattleExpendituresInDayOfWeek(battleId, memberInfo.getMemberId(), dayOfWeek));
    }
}
