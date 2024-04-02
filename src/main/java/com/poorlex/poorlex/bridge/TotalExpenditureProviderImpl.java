package com.poorlex.poorlex.bridge;

import com.poorlex.poorlex.consumption.expenditure.domain.ExpenditureRepository;
import com.poorlex.poorlex.consumption.weeklybudget.service.provider.TotalExpenditureProvider;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TotalExpenditureProviderImpl implements TotalExpenditureProvider {

    private final ExpenditureRepository expenditureRepository;

    @Override
    public Long byMemberIdBetween(final Long memberId, final LocalDate start, final LocalDate end) {
        return expenditureRepository.findSumExpenditureByMemberIdAndBetween(memberId, start, end);
    }
}
