package com.poorlex.refactoring.bridge;

import com.poorlex.refactoring.battle.battle.service.provider.BattleParticipantSumExpenditureProvider;
import com.poorlex.refactoring.expenditure.domain.ExpenditureRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BattleParticipantSumExpenditureProviderImpl implements BattleParticipantSumExpenditureProvider {

    private final ExpenditureRepository expenditureRepository;

    @Override
    public Long byMemberIdBetween(final Long memberId, final LocalDateTime battleStart, final LocalDateTime battleEnd) {
        return expenditureRepository.findTotalExpenditureByMemberIdBetweenDateTime(memberId, battleStart, battleEnd);
    }
}
