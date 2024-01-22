package com.poorlex.poorlex.expenditure.domain;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {

    @Query(value = "select coalesce(sum(e.amount.value), 0) from Expenditure e "
        + "where e.memberId = :memberId and e.date between :start and :end")
    int findSumExpenditureByMemberIdAndBetween(@Param(value = "memberId") final Long memberId,
                                               @Param(value = "start") final LocalDateTime start,
                                               @Param(value = "end") final LocalDateTime end);

    @Query(value = "select m.id as memberId, coalesce(sum(e.amount.value), 0) as totalExpenditure "
        + "from Member m left join Expenditure e on m.id = e.memberId and e.date between :start and :end "
        + "where m.id in :memberIds "
        + "group by m.id ")
    List<TotalExpenditureAndMemberIdDto> findTotalExpendituresBetweenAndMemberIdIn(
        @Param(value = "memberIds") final List<Long> memberIds,
        @Param(value = "start") final LocalDateTime start,
        @Param(value = "end") final LocalDateTime end
    );

    @Query("select e from Expenditure e "
        + "left join BattleParticipant bp on bp.battleId = :battleId and e.memberId = bp.memberId "
        + "left join Battle b on bp.battleId = b.id "
        + "where e.date between b.duration.start and b.duration.end")
    List<Expenditure> findBattleExpenditureByBattleId(final Long battleId);

    List<Expenditure> findExpendituresByMemberIdAndDateBetween(final Long memberId,
                                                               final LocalDateTime start,
                                                               final LocalDateTime end);

    List<Expenditure> findAllByMemberId(final Long memberId);
}
