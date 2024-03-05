package com.poorlex.refactoring.battle.history.service;

import com.poorlex.refactoring.battle.battle.service.dto.response.BattleSuccessCountResponse;
import com.poorlex.refactoring.battle.history.domain.BattleHistoryRepository;
import com.poorlex.refactoring.battle.history.domain.dto.BattlDifficultySuccessCountDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BattleHistoryQueryService {

    private final BattleHistoryRepository battleHistoryRepository;

    public BattleSuccessCountResponse findMemberBattleSuccessCounts(final Long memberId) {
        final List<BattlDifficultySuccessCountDto> battleSuccessCountsPerDifficulties =
            battleHistoryRepository.findDifficultySuccessCountsByMemberId(memberId);

        return new BattleSuccessCountResponse(battleSuccessCountsPerDifficulties);
    }
}
