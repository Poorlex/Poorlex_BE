package com.poolex.poolex.expenditure.service;

import com.poolex.poolex.config.event.Events;
import com.poolex.poolex.expenditure.domain.Expenditure;
import com.poolex.poolex.expenditure.domain.ExpenditureRepository;
import com.poolex.poolex.expenditure.domain.WeeklyExpenditureDuration;
import com.poolex.poolex.expenditure.service.dto.request.ExpenditureCreateRequest;
import com.poolex.poolex.expenditure.service.dto.request.MemberWeeklyTotalExpenditureRequest;
import com.poolex.poolex.expenditure.service.dto.response.MemberWeeklyTotalExpenditureResponse;
import com.poolex.poolex.expenditure.service.event.ExpenditureCreatedEvent;
import com.poolex.poolex.expenditure.service.mapper.ExpenditureMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExpenditureService {

    private final ExpenditureRepository expenditureRepository;

    @Transactional
    public Long createExpenditure(final Long memberId, final ExpenditureCreateRequest request) {
        final Expenditure expenditure = ExpenditureMapper.createRequestToExpenditure(memberId, request);
        final Expenditure savedExpenditure = expenditureRepository.save(expenditure);
        Events.raise(new ExpenditureCreatedEvent(memberId));

        return savedExpenditure.getId();
    }

    public MemberWeeklyTotalExpenditureResponse findMemberWeeklyTotalExpenditure(final Long memberId,
                                                                                 final MemberWeeklyTotalExpenditureRequest request) {
        final WeeklyExpenditureDuration duration = WeeklyExpenditureDuration.from(request.getDateTime());
        final int sumExpenditure = expenditureRepository.findSumExpenditureByMemberIdAndBetween(
            memberId,
            duration.getStart(),
            duration.getEnd()
        );

        return new MemberWeeklyTotalExpenditureResponse(sumExpenditure);
    }
}
