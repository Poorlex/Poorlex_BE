package com.poolex.poolex.battle.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BattleRepository extends JpaRepository<Battle, Long> {

    @Query(
        "select b from Battle b "
            + "left join BattleParticipant p "
            + "on p.battleId = b.id "
            + "where p.memberId = :memberId"
    )
    List<Battle> findBattlesByMemberId(@Param("memberId") final Long memberId);

    @Query(
        "select b, count(b) from Battle b "
            + "left join BattleParticipant p "
            + "on p.battleId = b.id "
            + "where p.memberId = :memberId "
            + "group by b.id"
    )
    List<BattleWithCurrentParticipantSize> findBattlesByMemberIdWithCurrentParticipantSize(
        @Param("memberId") final Long memberId
    );

    @Query(
        "select b as battle, count(b.id) as currentParticipantSize from Battle b "
            + "left join BattleParticipant p "
            + "on p.battleId = b.id "
            + "where b.status = :status "
            + "group by b.id"
    )
    List<BattleWithCurrentParticipantSize> findBattlesByStatusesWithCurrentParticipantSize(
        @Param("status") BattleStatus battleStatus
    );

    @Query(
        "select b as battle, count(b.id) as currentParticipantSize from Battle b "
            + "left join BattleParticipant p "
            + "on p.battleId = b.id "
            + "where b.status in :status "
            + "group by b.id"
    )
    List<BattleWithCurrentParticipantSize> findBattlesByStatusesWithCurrentParticipantSizeIn(
        @Param("status") List<BattleStatus> battleStatus
    );
}
