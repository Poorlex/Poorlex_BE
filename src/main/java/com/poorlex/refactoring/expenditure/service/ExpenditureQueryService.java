package com.poorlex.refactoring.expenditure.service;

import com.poorlex.refactoring.expenditure.domain.ExpenditureRepository;
import com.poorlex.refactoring.expenditure.domain.WeeklyExpenditureDuration;
import com.poorlex.refactoring.expenditure.service.dto.response.ExpenditureResponse;
import com.poorlex.refactoring.expenditure.service.dto.response.MemberWeeklyTotalExpenditureResponse;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExpenditureQueryService {

    private final ExpenditureRepository expenditureRepository;

    public ExpenditureResponse findExpenditure(final Long expenditureId) {
        return expenditureRepository.findById(expenditureId)
            .map(ExpenditureResponse::from)
            .orElseThrow(() -> new IllegalArgumentException("해당 Id 의 지출이 존재하지 않습니다."));
    }

    public List<ExpenditureResponse> findMemberExpenditures(final Long memberId) {
        return expenditureRepository.findAllByMemberId(memberId).stream()
            .map(ExpenditureResponse::from)
            .toList();
    }

    public MemberWeeklyTotalExpenditureResponse findMemberCurrentWeeklyTotalExpenditure(final Long memberId) {
        final Long weeklyTotalBudget = findMemberWeeklyTotalExpenditureInclude(memberId, LocalDate.now());

        return new MemberWeeklyTotalExpenditureResponse(weeklyTotalBudget);
    }

    public Long findMemberWeeklyTotalExpenditureInclude(final Long memberId, final LocalDate date) {
        final WeeklyExpenditureDuration duration = WeeklyExpenditureDuration.from(date);
        final LocalDate start = duration.getStart();
        final LocalDate end = duration.getEnd();

        return expenditureRepository.findTotalExpenditureByMemberIdBetweenDate(memberId, start, end);
    }
}
