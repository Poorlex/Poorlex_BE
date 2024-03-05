package com.poorlex.refactoring.bridge;

import com.poorlex.refactoring.expenditure.domain.ExpenditureRepository;
import com.poorlex.refactoring.expenditure.domain.WeeklyExpenditureDuration;
import com.poorlex.refactoring.user.member.service.provider.WeeklyExpenditureProvider;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WeeklyExpenditureProviderImpl implements WeeklyExpenditureProvider {

    private final ExpenditureRepository expenditureRepository;

    @Override
    public Long byMemberIdContains(final Long memberId, final LocalDate date) {
        final WeeklyExpenditureDuration duration = WeeklyExpenditureDuration.from(date);
        return expenditureRepository.findTotalExpenditureByMemberIdBetweenDate(memberId,
            duration.getStart(),
            duration.getEnd()
        );
    }
}
