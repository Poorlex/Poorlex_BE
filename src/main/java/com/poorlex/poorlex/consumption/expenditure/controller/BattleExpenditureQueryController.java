package com.poorlex.poorlex.consumption.expenditure.controller;

import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import com.poorlex.poorlex.consumption.expenditure.api.BattleExpenditureQueryControllerSwaggerInterface;
import com.poorlex.poorlex.consumption.expenditure.service.ExpenditureQueryService;
import com.poorlex.poorlex.consumption.expenditure.service.dto.response.BattleExpenditureResponse;
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
public class BattleExpenditureQueryController implements BattleExpenditureQueryControllerSwaggerInterface {

    private final ExpenditureQueryService expenditureQueryService;


    @GetMapping
    public ResponseEntity<List<BattleExpenditureResponse>> findMemberBattleExpenditures(
            @MemberOnly final MemberInfo memberInfo,
            @PathVariable(name = "battleId") final Long battleId
    ) {
        return ResponseEntity.ok()
                .body(expenditureQueryService.findMemberBattleExpenditures(battleId, memberInfo.getMemberId()));
    }

    @GetMapping(params = "dayOfWeek")
    public ResponseEntity<List<BattleExpenditureResponse>> findBattleExpenditures(
            @MemberOnly final MemberInfo memberInfo,
            @PathVariable(name = "battleId") final Long battleId,
            @RequestParam(name = "dayOfWeek") final String dayOfWeek
    ) {
        return ResponseEntity.ok()
                .body(expenditureQueryService.findBattleExpendituresInDayOfWeek(battleId,
                                                                                memberInfo.getMemberId(),
                                                                                dayOfWeek));
    }
}
