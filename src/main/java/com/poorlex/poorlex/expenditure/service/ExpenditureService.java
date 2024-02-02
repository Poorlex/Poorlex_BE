package com.poorlex.poorlex.expenditure.service;

import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleRepository;
import com.poorlex.poorlex.config.event.Events;
import com.poorlex.poorlex.expenditure.domain.Expenditure;
import com.poorlex.poorlex.expenditure.domain.ExpenditureRepository;
import com.poorlex.poorlex.expenditure.domain.TotalExpenditureAndMemberIdDto;
import com.poorlex.poorlex.expenditure.domain.WeeklyExpenditureDuration;
import com.poorlex.poorlex.expenditure.service.dto.RankAndTotalExpenditureDto;
import com.poorlex.poorlex.expenditure.service.dto.request.ExpenditureCreateRequest;
import com.poorlex.poorlex.expenditure.service.dto.request.ExpenditureUpdateRequest;
import com.poorlex.poorlex.expenditure.service.dto.request.MemberWeeklyTotalExpenditureRequest;
import com.poorlex.poorlex.expenditure.service.dto.response.BattleExpenditureResponse;
import com.poorlex.poorlex.expenditure.service.dto.response.ExpenditureResponse;
import com.poorlex.poorlex.expenditure.service.dto.response.MemberWeeklyTotalExpenditureResponse;
import com.poorlex.poorlex.expenditure.service.event.ExpenditureCreatedEvent;
import com.poorlex.poorlex.expenditure.service.event.ZeroExpenditureCreatedEvent;
import com.poorlex.poorlex.expenditure.service.mapper.ExpenditureMapper;
import java.time.DayOfWeek;
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

    private final BattleRepository battleRepository;
    private final ExpenditureRepository expenditureRepository;

    @Transactional
    public Long createExpenditure(final Long memberId, final ExpenditureCreateRequest request) {
        final Expenditure expenditure = expenditureRepository.save(
            ExpenditureMapper.createRequestToExpenditure(memberId, request)
        );
        raiseEvent(expenditure.getAmount(), memberId);
        return expenditure.getId();
    }

    private void raiseEvent(final long expenditureAmount, final Long memberId) {
        if (expenditureAmount == 0) {
            Events.raise(new ZeroExpenditureCreatedEvent(memberId));
            return;
        }
        Events.raise(new ExpenditureCreatedEvent(memberId));
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

    public ExpenditureResponse findExpenditureById(final Long expenditureId) {
        return expenditureRepository.findById(expenditureId)
            .map(ExpenditureResponse::from)
            .orElseThrow(() -> new IllegalArgumentException("해당 Id 의 지출이 존재하지 않습니다."));
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
        final DayOfWeek targetDayOfWeek = DayOfWeek.valueOf(dayOfWeek);
        final List<Expenditure> battleExpenditures = expenditureRepository.findBattleExpenditureByBattleId(battleId);

        return battleExpenditures.stream()
            .filter(expenditure -> expenditure.getDate().getDayOfWeek() == targetDayOfWeek)
            .map(expenditure -> BattleExpenditureResponse.from(expenditure, expenditure.hasSameMemberId(memberId)))
            .toList();
    }

    public List<BattleExpenditureResponse> findMemberBattleExpenditures(final Long battleId, final Long memberId) {
        final Battle battle = battleRepository.findById(battleId)
            .orElseThrow(() -> new IllegalArgumentException("Id에 해당하는 배틀이 없습니다."));

        final List<Expenditure> expenditures = expenditureRepository.findExpendituresByMemberIdAndDateBetween(
            memberId,
            battle.getDuration().getStart(),
            battle.getDuration().getEnd()
        );

        return expenditures.stream()
            .map(expenditure -> BattleExpenditureResponse.from(expenditure, true))
            .toList();
    }

    public void updateExpenditure(final Long memberId,
                                  final Long expenditureId,
                                  final ExpenditureUpdateRequest request) {
        final Expenditure expenditure = expenditureRepository.findById(expenditureId)
            .orElseThrow(() -> new IllegalArgumentException("Id에 해당하는 지출이 없습니다."));

        validateExpenditureOwnership(memberId, expenditure);
        expenditure.pasteAmountAndDescriptionAndImageUrls(
            ExpenditureMapper.createRequestToExpenditure(memberId, request)
        );
    }

    private void validateExpenditureOwnership(final Long memberId, final Expenditure expenditure) {
        if (!expenditure.hasSameMemberId(memberId)) {
            throw new IllegalArgumentException("다른 회원의 지출은 수정할 수 없습니다.");
        }
    }
}
