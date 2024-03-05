package com.poorlex.refactoring.battle.battle.domain;

import com.poorlex.refactoring.battle.battle.service.dto.BattleParticipantWithExpenditureDto;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenditureRankings {

    private final Map<Long, Integer> rankingMap;

    ExpenditureRankings(final Map<Long, Integer> rankingMap) {
        this.rankingMap = rankingMap;
    }

    public static ExpenditureRankings from(final List<BattleParticipantWithExpenditureDto> participants) {
        final List<BattleParticipantWithExpenditureDto> sortedByExpenditure = participants.stream()
            .sorted(Comparator.comparingLong(BattleParticipantWithExpenditureDto::getExpenditure))
            .toList();

        final Map<Long, Integer> rankingMap = new HashMap<>();

        int rank = 0;
        long maxExpenditure = -1L;
        for (BattleParticipantWithExpenditureDto participant : sortedByExpenditure) {
            if (participant.getExpenditure() > maxExpenditure) {
                rank++;
                maxExpenditure = participant.getExpenditure();
            }
            rankingMap.put(participant.getMemberId(), rank);
        }

        return new ExpenditureRankings(rankingMap);
    }

    public int getByMemberId(final Long memberId) {
        return rankingMap.get(memberId);
    }
}
