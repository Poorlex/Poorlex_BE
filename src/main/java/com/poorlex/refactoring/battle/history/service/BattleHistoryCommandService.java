package com.poorlex.refactoring.battle.history.service;

import com.poorlex.refactoring.battle.battle.domain.Battle;
import com.poorlex.refactoring.battle.battle.domain.BattleRepository;
import com.poorlex.refactoring.battle.history.domain.BattleDifficulty;
import com.poorlex.refactoring.battle.history.domain.BattleHistory;
import com.poorlex.refactoring.battle.history.domain.BattleHistoryRepository;
import com.poorlex.refactoring.battle.history.domain.BattleParticipantRanking;
import com.poorlex.refactoring.battle.history.service.dto.ParticipantTotalExpenditureDto;
import com.poorlex.refactoring.battle.history.service.provider.BattleParticipantTotalExpenditureProvider;
import com.poorlex.refactoring.battle.history.service.provider.BattleParticipantsMemberIdProvider;
import com.poorlex.refactoring.battle.history.service.provider.BattleSuccessPointProvider;
import com.poorlex.refactoring.battle.history.service.validation.BattleHistoryException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BattleHistoryCommandService {

    private final BattleRepository battleRepository;
    private final BattleHistoryRepository battleHistoryRepository;
    private final BattleSuccessPointProvider battleSuccessPointProvider;
    private final BattleParticipantsMemberIdProvider battleParticipantsMemberIdProvider;
    private final BattleParticipantTotalExpenditureProvider battleParticipantTotalExpenditureProvider;

    public void saveBattleHistory(final Long battleId) {
        final Battle battle = battleRepository.findById(battleId)
            .orElseThrow(BattleHistoryException.BattleNotExistException::new);
        final List<BattleHistory> mappedBattleHistories =
            mapToBattleHistories(getParticipantTotalExpenditures(battleId, battle), battle);

        mappedBattleHistories.forEach(battleHistoryRepository::save);
    }

    private List<ParticipantTotalExpenditureDto> getParticipantTotalExpenditures(final Long battleId,
                                                                                 final Battle battle) {
        final List<Long> participantsMemberId = battleParticipantsMemberIdProvider.byBattleId(battleId);

        return battleParticipantTotalExpenditureProvider.byParticipantMemberIdAndBetween(
            participantsMemberId,
            battle.getStart(),
            battle.getEnd()
        );
    }

    private List<BattleHistory> mapToBattleHistories(
        final List<ParticipantTotalExpenditureDto> participantTotalExpenditures,
        final Battle battle
    ) {
        final BattleParticipantRanking ranking = BattleParticipantRanking.from(participantTotalExpenditures);

        return participantTotalExpenditures.stream()
            .map(
                battleParticipant -> {
                    final Long memberId = battleParticipant.getMemberId();
                    final Long sumExpenditure = battleParticipant.getSumExpenditure();
                    final int rank = ranking.getByMemberId(battleParticipant.getMemberId());

                    return mapToBattleHistory(memberId, sumExpenditure, rank, battle);
                }
            )
            .toList();
    }

    private BattleHistory mapToBattleHistory(final Long memberId,
                                             final Long expenditure,
                                             final int rank,
                                             final Battle battle) {
        return BattleHistory.withoutId(
            memberId,
            battle.getName(),
            battle.getImageUrl(),
            battle.getBudgetLeft(expenditure),
            rank,
            battle.getBattleParticipantLimit(),
            battleSuccessPointProvider.getPointBy(battle.getId(), rank),
            LocalDate.from(battle.getEnd()),
            BattleDifficulty.findByName(battle.getDifficulty()),
            battle.isSuccess(expenditure)
        );
    }
}
