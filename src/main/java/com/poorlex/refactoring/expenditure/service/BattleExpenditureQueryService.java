package com.poorlex.refactoring.expenditure.service;

import com.poorlex.refactoring.battle.battle.domain.Battle;
import com.poorlex.refactoring.battle.battle.domain.BattleRepository;
import com.poorlex.refactoring.expenditure.domain.Expenditure;
import com.poorlex.refactoring.expenditure.domain.ExpenditureRepository;
import com.poorlex.refactoring.expenditure.service.dto.response.BattleExpenditureResponse;
import java.time.DayOfWeek;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BattleExpenditureQueryService {

    private final BattleRepository battleRepository;
    private final ExpenditureRepository expenditureRepository;

    public List<BattleExpenditureResponse> findBattleExpendituresInDayOfWeek(final Long battleId,
                                                                             final Long memberId,
                                                                             final String dayOfWeek) {
        final DayOfWeek targetDayOfWeek = DayOfWeek.valueOf(dayOfWeek.toUpperCase());
        final List<Expenditure> battleExpenditures = expenditureRepository.findBattleExpenditureByBattleId(battleId);

        return battleExpenditures.stream()
            .filter(expenditure -> expenditure.getDate().getDayOfWeek().equals(targetDayOfWeek))
            .map(expenditure -> BattleExpenditureResponse.from(expenditure, expenditure.isCreatedBy(memberId)))
            .toList();
    }

    public List<BattleExpenditureResponse> findMemberBattleExpenditures(final Long battleId, final Long memberId) {
        final Battle battle = battleRepository.findById(battleId)
            .orElseThrow(() -> new IllegalArgumentException("Id에 해당하는 배틀이 없습니다."));

        final List<Expenditure> expenditures = expenditureRepository.findExpendituresByMemberIdAndDateBetween(
            memberId,
            battle.getStart(),
            battle.getEnd()
        );

        final boolean createdByMember = true;
        return expenditures.stream()
            .map(expenditure -> BattleExpenditureResponse.from(expenditure, createdByMember))
            .toList();
    }
}
