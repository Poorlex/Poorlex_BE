package com.poorlex.refactoring.battle.history.service.provider.implementation;

import com.poorlex.refactoring.battle.battle.domain.BattleDifficulty;
import com.poorlex.refactoring.battle.history.domain.BattleHistoryRepository;
import com.poorlex.refactoring.battle.history.domain.dto.BattlDifficultySuccessCountDto;
import com.poorlex.refactoring.user.member.service.dto.BattleDifficultySuccessCountDto;
import com.poorlex.refactoring.user.member.service.provider.MemberBattleSuccessCountProvider;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberBattleSuccessCountProviderImpl implements MemberBattleSuccessCountProvider {

    private final BattleHistoryRepository battleHistoryRepository;

    @Override
    public BattleDifficultySuccessCountDto byMemberId(final Long memberId) {
        final List<BattlDifficultySuccessCountDto> difficultySuccessCounts =
            battleHistoryRepository.findDifficultySuccessCountsByMemberId(memberId);
        int easySuccessCount = 0;
        int normalSuccessCount = 0;
        int hardSuccessCount = 0;

        for (final BattlDifficultySuccessCountDto difficultySuccessCount : difficultySuccessCounts) {
            final BattleDifficulty difficulty = difficultySuccessCount.getDifficulty();
            final int successCount = difficultySuccessCount.getSuccessCount();
            if (difficulty == BattleDifficulty.EASY) {
                easySuccessCount += successCount;
            }
            if (difficulty == BattleDifficulty.NORMAL) {
                normalSuccessCount += successCount;
            }
            if (difficulty == BattleDifficulty.HARD) {
                hardSuccessCount += successCount;
            }
        }
        return new BattleDifficultySuccessCountDto(easySuccessCount, normalSuccessCount, hardSuccessCount);
    }
}
