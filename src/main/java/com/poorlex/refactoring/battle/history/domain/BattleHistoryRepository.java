package com.poorlex.refactoring.battle.history.domain;

import com.poorlex.refactoring.battle.history.domain.dto.BattlDifficultySuccessCountDto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BattleHistoryRepository extends JpaRepository<BattleHistory, Long> {

    @Query(
        "select count(bs) as successCount, bs.battleDifficulty as difficulty "
            + "from BattleHistory bs "
            + "where bs.memberId = :memberId "
            + "group by bs.battleDifficulty"
    )
    List<BattlDifficultySuccessCountDto> findDifficultySuccessCountsByMemberId(final Long memberId);

    List<BattleHistory> findBattleHistoriesByMemberId(final Long memberId);

    List<BattleHistory> findBattleSuccessHistoriesByMemberId(final Long memberId);
}
