package com.poorlex.refactoring.bridge;

import com.poorlex.refactoring.battle.alarm.service.dto.ExpenditureDurationDto;
import com.poorlex.refactoring.battle.alarm.service.provider.MemberSumExpenditureProvider;
import com.poorlex.refactoring.expenditure.domain.ExpenditureRepository;
import com.poorlex.refactoring.expenditure.domain.WeeklyExpenditureDuration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberSumExpenditureProviderImpl implements MemberSumExpenditureProvider {

    private final ExpenditureRepository expenditureRepository;

    @Override
    public Long getMemberWeeklySumExpenditureInclude(final Long memberId, final LocalDate date) {
        final WeeklyExpenditureDuration weekDuration = WeeklyExpenditureDuration.from(date);
        return expenditureRepository.findTotalExpenditureByMemberIdBetweenDate(
            memberId,
            weekDuration.getStart(),
            weekDuration.getEnd()
        );
    }

    @Override
    public Long betweenBattleDurationInSameOrderWithDurations(final Long memberId, final LocalDateTime start,
                                                              final LocalDateTime end) {
        return expenditureRepository.findTotalExpenditureByMemberIdBetweenDateTime(memberId, start, end);
    }

    @Override
    public List<Long> betweenBattleDurationInSameOrderWithDurations(final Long memberId,
                                                                    final List<ExpenditureDurationDto> durations) {
        return durations.stream()
            .map(duration -> betweenBattleDurationInSameOrderWithDurations(memberId, duration.getStart(),
                duration.getEnd()))
            .toList();
    }
}
