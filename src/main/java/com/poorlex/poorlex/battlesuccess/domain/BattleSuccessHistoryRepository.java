package com.poorlex.poorlex.battlesuccess.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BattleSuccessHistoryRepository extends JpaRepository<BattleSuccessHistory, Long> {

    @Query(
        "select count(bs) as successCount, bs.battleDifficulty as difficulty   from BattleSuccessHistory bs "
            + "where bs.memberId = :memberId "
            + "group by bs.battleDifficulty"
    )
    List<BattleSuccessCountGroup> findDifficultySuccessCountsByMemberId(final Long memberId);
}
