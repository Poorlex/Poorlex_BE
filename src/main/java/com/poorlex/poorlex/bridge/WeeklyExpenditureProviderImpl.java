package com.poorlex.poorlex.bridge;

import com.poorlex.poorlex.consumption.expenditure.domain.ExpenditureRepository;
import com.poorlex.poorlex.consumption.expenditure.domain.WeeklyExpenditureDuration;
import com.poorlex.poorlex.user.member.service.provider.WeeklyExpenditureProvider;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WeeklyExpenditureProviderImpl implements WeeklyExpenditureProvider {

    private final ExpenditureRepository expenditureRepository;

    @Override
    public Long getByMemberId(final Long memberId, final LocalDate date) {
        final WeeklyExpenditureDuration duration = WeeklyExpenditureDuration.from(date);

        return getSumExpenditureByMemberIdAndBetween(memberId, duration);
    }

    @Override
    public Map<Long, Long> getByMemberIds(final List<Long> memberIds, final LocalDate date) {
        final WeeklyExpenditureDuration duration = WeeklyExpenditureDuration.from(date);
        final ArrayList<Long> memberIdList = new ArrayList<>(memberIds);
        return memberIdList.stream()
                .collect(Collectors.toMap(id -> id, id -> getSumExpenditureByMemberIdAndBetween(id, duration)));
    }

    private Long getSumExpenditureByMemberIdAndBetween(final Long id, final WeeklyExpenditureDuration duration) {
        return expenditureRepository.findSumExpenditureByMemberIdAndBetween(id,
                                                                            duration.getStart(),
                                                                            duration.getEnd());
    }
}
