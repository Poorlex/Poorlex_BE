package com.poorlex.refactoring.battle.alarm.service.provider;

import com.poorlex.refactoring.battle.alarm.service.dto.ExpenditureDurationDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MemberSumExpenditureProvider {

    Long getMemberWeeklySumExpenditureInclude(final Long memberId, final LocalDate date);

    Long betweenBattleDurationInSameOrderWithDurations(final Long memberId,
                                                       final LocalDateTime start,
                                                       final LocalDateTime end);

    List<Long> betweenBattleDurationInSameOrderWithDurations(final Long memberId,
                                                             final List<ExpenditureDurationDto> durations);
}
