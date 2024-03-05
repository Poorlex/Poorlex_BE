package com.poorlex.refactoring.bridge;

import com.poorlex.refactoring.battle.history.service.dto.ParticipantTotalExpenditureDto;
import com.poorlex.refactoring.battle.history.service.provider.BattleParticipantTotalExpenditureProvider;
import com.poorlex.refactoring.expenditure.domain.ExpenditureRepository;
import com.poorlex.refactoring.expenditure.domain.TotalExpenditureAndMemberIdDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BattleParticipantTotalExpenditureProviderImpl implements BattleParticipantTotalExpenditureProvider {

    private final ExpenditureRepository expenditureRepository;

    @Override
    public List<ParticipantTotalExpenditureDto> byParticipantMemberIdAndBetween(final List<Long> memberIds,
                                                                                final LocalDateTime start,
                                                                                final LocalDateTime end) {
        final List<TotalExpenditureAndMemberIdDto> totalExpenditures =
            expenditureRepository.findTotalExpendituresBetweenAndMemberIdIn(memberIds, start, end);

        return totalExpenditures.stream()
            .map(
                totalExpenditure -> new ParticipantTotalExpenditureDto(
                    totalExpenditure.getTotalExpenditure(),
                    totalExpenditure.getTotalExpenditure()
                )
            )
            .toList();
    }
}
