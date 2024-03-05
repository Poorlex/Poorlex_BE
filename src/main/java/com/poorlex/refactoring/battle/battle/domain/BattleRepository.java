package com.poorlex.refactoring.battle.battle.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BattleRepository extends JpaRepository<Battle, Long> {

    @Query(
        "select b as battle, count(p) as currentParticipantSize from Battle b "
            + "left join BattleParticipant p "
            + "on p.battleId = b.id "
            + "where b.status in :statuses "
            + "group by b.id"
    )
    List<BattleWithCurrentParticipantSize> findBattlesWithCurrentParticipantSizeByStatusesIn(
        @Param("status") List<BattleStatus> statuses
    );

    boolean existsBattleById(final Long id);

    @Query(
        "select count(b) from Battle b "
            + "join BattleParticipant bp on bp.battleId = b.id and bp.memberId = :memberId "
            + "where b.status in :statuses "
    )
    int countMemberBattleWithStatuses(final Long memberId, final List<BattleStatus> statuses);

    @Query(
        "select b from Battle b "
            + "join BattleParticipant bp on bp.battleId = b.id and bp.memberId = :memberId "
            + "where b.status = :status "
    )
    List<Battle> findMemberBattlesByMemberIdAndStatus(
        @Param("memberId") final Long memberId,
        @Param("status") final BattleStatus status
    );
}
