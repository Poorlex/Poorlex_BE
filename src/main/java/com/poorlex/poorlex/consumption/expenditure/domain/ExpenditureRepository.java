package com.poorlex.poorlex.consumption.expenditure.domain;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {

    @Query(value = "select coalesce(sum(e.amount.value), 0) from Expenditure e "
            + "where e.memberId = :memberId and e.date >= :start and e.date <= :end")
    Long findSumExpenditureByMemberIdAndBetween(@Param(value = "memberId") final Long memberId,
                                                @Param(value = "start") final LocalDate start,
                                                @Param(value = "end") final LocalDate end);

    @Query(value = "select m.id as memberId, coalesce(sum(e.amount.value), 0) as totalExpenditure "
            + "from Member m left join Expenditure e on m.id = e.memberId and e.date between :start and :end "
            + "where m.id in :memberIds "
            + "group by m.id ")
    List<TotalExpenditureAndMemberIdDto> findTotalExpendituresBetweenAndMemberIdIn(
            @Param(value = "memberIds") final List<Long> memberIds,
            @Param(value = "start") final LocalDate start,
            @Param(value = "end") final LocalDate end
    );

    @Query("select e from Expenditure e "
            + "left join BattleParticipant bp on e.memberId = bp.memberId and bp.battleId = :battleId "
            + "left join Battle b on bp.battleId = b.id "
            + "where e.date between cast(b.duration.start as LocalDate) and cast(b.duration.end as LocalDate)")
    List<Expenditure> findBattleExpenditureByBattleId(@Param("battleId") final Long battleId);

    List<Expenditure> findExpendituresByMemberIdAndDateBetween(final Long memberId,
                                                               final LocalDate start,
                                                               final LocalDate end);

    boolean existsByMemberIdAndDate(Long memberId, LocalDate date);
    boolean existsByIdNotAndMemberIdAndDate(Long id, Long memberId, LocalDate date);

    List<Expenditure> findAllByMemberId(final Long memberId);

    List<Expenditure> findAllByMemberIdOrderByCreatedAtDesc(final Long memberId, final Pageable pageable);

    Long countAllByMemberId(final Long memberId);

    List<Expenditure> findAllByPointPaid(final boolean pointPaid);
}
