package com.poorlex.refactoring.battle.history.service.provider.implementation;

import com.poorlex.refactoring.battle.battle.service.dto.BattleHistoryDto;
import com.poorlex.refactoring.battle.battle.service.provider.CompleteBattleHistoryProvider;
import com.poorlex.refactoring.battle.history.domain.BattleHistory;
import com.poorlex.refactoring.battle.history.domain.BattleHistoryRepository;
import com.poorlex.refactoring.battle.history.service.mapper.BattleHistoryDtoMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompleteBattleHistoryProviderImpl implements CompleteBattleHistoryProvider {

    private final BattleHistoryRepository battleHistoryRepository;

    @Override
    public List<BattleHistoryDto> getByMemberId(final Long memberId) {
        final List<BattleHistory> battleHistories = battleHistoryRepository.findBattleHistoriesByMemberId(memberId);
        return BattleHistoryDtoMapper.mapBy(battleHistories);
    }
}
