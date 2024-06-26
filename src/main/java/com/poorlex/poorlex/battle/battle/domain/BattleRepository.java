package com.poorlex.poorlex.battle.battle.domain;

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
            "select b, count(p) from Battle b "
                    + "left join BattleParticipant p "
                    + "on p.battleId = b.id "
                    + "where p.memberId = :memberId "
                    + "group by b.id"
    )
    List<BattleWithCurrentParticipantSize> findBattlesByMemberIdWithCurrentParticipantSize(
            @Param("memberId") final Long memberId
    );

    @Query(
            "select b as battle, count(p) as currentParticipantSize from Battle b "
                    + "left join BattleParticipant p "
                    + "on p.battleId = b.id "
                    + "where b.status = :status "
                    + "group by b.id"
    )
    List<BattleWithCurrentParticipantSize> findBattlesByStatusesWithCurrentParticipantSize(
            @Param("status") BattleStatus battleStatus
    );

    @Query(
            "select b as battle, count(p) as currentParticipantSize from Battle b "
                    + "left join BattleParticipant p "
                    + "on p.battleId = b.id "
                    + "where b.status in :status "
                    + "group by b.id"
    )
    List<BattleWithCurrentParticipantSize> findBattlesWithCurrentParticipantSizeByStatusesIn(
            @Param("status") List<BattleStatus> battleStatus
    );

    @Query(
            "select b as battle, p as participant from Battle b "
                    + "left join BattleParticipant p "
                    + "on b.id = p.battleId "
                    + "where p.memberId = :memberId and b.status = :status"
    )
    List<BattleWithMemberExpenditure> findMemberBattlesByMemberIdAndStatus(
            @Param("memberId") final Long memberId,
            @Param("status") final BattleStatus status
    );

    @Query(
            "select b as battle, sum(coalesce(e.amount.value, 0)) as expenditure from Battle b "
                    + "left join BattleParticipant p on p.battleId = b.id "
                    + "left join Expenditure e on e.memberId = p.memberId "
                    + "and e.date between cast(b.duration.start as LocalDate) and cast(b.duration.end as LocalDate) "
                    + "where b.status in :status "
                    + "and p.memberId = :memberId "
                    + "group by b.id"
    )
    List<BattleWithMemberExpenditure> findMemberBattlesByMemberIdAndStatusWithExpenditure(
            @Param("memberId") final Long memberId,
            @Param("status") final List<BattleStatus> status
    );

    @Query("select p as battleParticipant, sum(coalesce(e.amount.value, 0)) as expenditure, count(e) as expenditureCount from Battle b "
            + "left join BattleParticipant p on b.id = p.battleId "
            + "left join Expenditure e on e.memberId = p.memberId "
            + "and e.date between cast(b.duration.start as LocalDate) and cast(b.duration.end as LocalDate) "
            + "where b.id = :battleId "
            + "group by p.id"
    )
    List<BattleParticipantWithExpenditure> findBattleParticipantsWithExpenditureByBattleId(
            @Param("battleId") final Long battleId
    );

    @Query(
            "select count(b) from Battle b "
                    + "left join BattleParticipant p on b.id = p.battleId "
                    + "where b.status in :statuses "
                    + "and p.memberId = :memberId"
    )
    int countMemberBattleWithStatuses(@Param("memberId") final Long memberId, @Param("statuses") final List<BattleStatus> statuses);

    @Query("select b.name.value from Battle b where b.id = :id")
    String findBattleNameById(@Param("id") final Long id);

    List<Battle> findBattlesByIdIn(final List<Long> ids);

    List<Battle> findBattlesByStatusIn(final List<BattleStatus> statuses);
}
