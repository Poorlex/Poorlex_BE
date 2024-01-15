package com.poolex.poolex.expenditure.service;

import com.poolex.poolex.config.event.Events;
import com.poolex.poolex.expenditure.domain.Expenditure;
import com.poolex.poolex.expenditure.domain.ExpenditureRepository;
import com.poolex.poolex.expenditure.domain.TotalExpenditureAndMemberIdDto;
import com.poolex.poolex.expenditure.domain.WeeklyExpenditureDuration;
import com.poolex.poolex.expenditure.service.dto.RankAndTotalExpenditureDto;
import com.poolex.poolex.expenditure.service.dto.request.ExpenditureCreateRequest;
import com.poolex.poolex.expenditure.service.dto.request.MemberWeeklyTotalExpenditureRequest;
import com.poolex.poolex.expenditure.service.dto.response.MemberWeeklyTotalExpenditureResponse;
import com.poolex.poolex.expenditure.service.event.ExpenditureCreatedEvent;
import com.poolex.poolex.expenditure.service.mapper.ExpenditureMapper;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public Map<Long, RankAndTotalExpenditureDto> getMembersTotalExpenditureRankBetween(final List<Long> memberIds,
                                                                                       final LocalDateTime start,
                                                                                       final LocalDateTime end) {
        final List<TotalExpenditureAndMemberIdDto> totalExpenditureAndMemberIdSortedByExpenditure =
            expenditureRepository.findTotalExpendituresBetweenAndMemberIdIn(memberIds, start, end)
                .stream()
                .sorted(Comparator.comparingLong(TotalExpenditureAndMemberIdDto::getTotalExpenditure))
                .toList();

        final Map<Long, RankAndTotalExpenditureDto> participantIdsAndRank = new HashMap<>();

        int rank = 0;
        Long prevExpenditure = 0L;
        int duplicateCount = 1;
        for (int idx = 0; idx < totalExpenditureAndMemberIdSortedByExpenditure.size(); idx++) {
            final TotalExpenditureAndMemberIdDto current = totalExpenditureAndMemberIdSortedByExpenditure.get(idx);
            final Long currentExpenditure = current.getTotalExpenditure();

            if (idx == 0) {
                rank++;
            } else if (currentExpenditure.equals(prevExpenditure)) {
                duplicateCount++;
            } else if (currentExpenditure > prevExpenditure) {
                rank += duplicateCount;
                duplicateCount = 1;
            }
            participantIdsAndRank.put(current.getMemberId(), new RankAndTotalExpenditureDto(rank, currentExpenditure));
            prevExpenditure = currentExpenditure;
        }

        return participantIdsAndRank;
    }
}
