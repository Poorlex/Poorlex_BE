package com.poorlex.refactoring.battle.history.domain;

import com.poorlex.refactoring.battle.history.service.dto.ParticipantTotalExpenditureDto;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BattleParticipantRanking {

    private final Map<Long, Integer> rankingMap;

    public BattleParticipantRanking(final Map<Long, Integer> rankingMap) {
        this.rankingMap = rankingMap;
    }

    public static BattleParticipantRanking from(final List<ParticipantTotalExpenditureDto> participants) {
        final List<ParticipantTotalExpenditureDto> sortedByExpenditure = participants.stream()
            .sorted(Comparator.comparingLong(ParticipantTotalExpenditureDto::getSumExpenditure))
            .toList();

        final Map<Long, Integer> rankingMap = new HashMap<>();

        int rank = 0;
        long maxExpenditure = -1L;
        for (ParticipantTotalExpenditureDto participant : sortedByExpenditure) {
            final Long currentSumExpenditure = participant.getSumExpenditure();
            if (currentSumExpenditure > maxExpenditure) {
                rank++;
                maxExpenditure = currentSumExpenditure;
            }
            rankingMap.put(participant.getMemberId(), rank);
        }

        return new BattleParticipantRanking(rankingMap);
    }

    public int getByMemberId(final Long memberId) {
        return rankingMap.get(memberId);
    }
}
