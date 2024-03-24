package com.poorlex.poorlex.expenditure.service;

import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleRepository;
import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.exception.ExceptionTag;
import com.poorlex.poorlex.expenditure.domain.Expenditure;
import com.poorlex.poorlex.expenditure.domain.ExpenditureRepository;
import com.poorlex.poorlex.expenditure.domain.TotalExpenditureAndMemberIdDto;
import com.poorlex.poorlex.expenditure.domain.WeeklyExpenditureDuration;
import com.poorlex.poorlex.expenditure.service.dto.RankAndTotalExpenditureDto;
import com.poorlex.poorlex.expenditure.service.dto.response.BattleExpenditureResponse;
import com.poorlex.poorlex.expenditure.service.dto.response.ExpenditureResponse;
import com.poorlex.poorlex.expenditure.service.dto.response.MemberWeeklyTotalExpenditureResponse;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ExpenditureQueryService {

    private final BattleRepository battleRepository;
    private final ExpenditureRepository expenditureRepository;

    public ExpenditureQueryService(final BattleRepository battleRepository,
                                   final ExpenditureRepository expenditureRepository) {
        this.battleRepository = battleRepository;
        this.expenditureRepository = expenditureRepository;
    }


    public MemberWeeklyTotalExpenditureResponse findMemberCurrentWeeklyTotalExpenditure(final Long memberId) {
        return findMemberWeeklyTotalExpenditure(memberId, LocalDateTime.now());
    }

    public MemberWeeklyTotalExpenditureResponse findMemberWeeklyTotalExpenditure(final Long memberId,
                                                                                 final LocalDateTime dateTime) {
        final WeeklyExpenditureDuration duration = WeeklyExpenditureDuration.from(dateTime);
        final int sumExpenditure = expenditureRepository.findSumExpenditureByMemberIdAndBetween(
                memberId,
                LocalDate.from(duration.getStart()),
                LocalDate.from(duration.getEnd())
        );

        return new MemberWeeklyTotalExpenditureResponse(sumExpenditure);
    }

    public Map<Long, RankAndTotalExpenditureDto> getMembersTotalExpenditureRankBetween(final List<Long> memberIds,
                                                                                       final LocalDateTime start,
                                                                                       final LocalDateTime end) {
        final LocalDate startDate = LocalDate.from(start);
        final LocalDate endDate = LocalDate.from(end);

        final List<TotalExpenditureAndMemberIdDto> totalExpenditureAndMemberIdSortedByExpenditure =
                expenditureRepository.findTotalExpendituresBetweenAndMemberIdIn(memberIds, startDate, endDate)
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

    public ExpenditureResponse findExpenditureById(final Long expenditureId) {
        return expenditureRepository.findById(expenditureId)
                .map(ExpenditureResponse::from)
                .orElseThrow(() -> {
                    final String errorMessage = String.format("Id에 해당 지출이 없습니다. ( 지출 ID : %d )", expenditureId);
                    return new ApiException(ExceptionTag.EXPENDITURE_FIND, errorMessage);
                });
    }

    public List<ExpenditureResponse> findMemberExpenditures(final Long memberId) {
        final List<Expenditure> memberExpenditures = expenditureRepository.findAllByMemberId(memberId);

        return memberExpenditures.stream()
                .map(ExpenditureResponse::from)
                .toList();
    }

    public List<BattleExpenditureResponse> findBattleExpendituresInDayOfWeek(final Long battleId,
                                                                             final Long memberId,
                                                                             final String dayOfWeek) {
        final DayOfWeek targetDayOfWeek = DayOfWeek.valueOf(dayOfWeek.toUpperCase());
        final List<Expenditure> battleExpenditures = expenditureRepository.findBattleExpenditureByBattleId(battleId);

        return battleExpenditures.stream()
                .filter(expenditure -> expenditure.getDate().getDayOfWeek() == targetDayOfWeek)
                .map(expenditure -> BattleExpenditureResponse.from(expenditure, expenditure.owned(memberId)))
                .toList();
    }

    public List<BattleExpenditureResponse> findMemberBattleExpenditures(final Long battleId, final Long memberId) {
        final Battle battle = battleRepository.findById(battleId)
                .orElseThrow(() -> {
                    final String errorMessage = String.format("Id에 해당하는 배틀이 없습니다. ( 배틀 Id : %d )", battleId);
                    return new ApiException(ExceptionTag.BATTLE_FIND, errorMessage);
                });

        final List<Expenditure> expenditures = expenditureRepository.findExpendituresByMemberIdAndDateBetween(
                memberId,
                LocalDate.from(battle.getDuration().getStart()),
                LocalDate.from(battle.getDuration().getEnd())
        );

        return expenditures.stream()
                .map(expenditure -> BattleExpenditureResponse.from(expenditure, true))
                .toList();
    }
}
