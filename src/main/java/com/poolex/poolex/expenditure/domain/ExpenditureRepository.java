package com.poolex.poolex.expenditure.domain;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {

    @Query(value = "select coalesce(sum(e.amount.value), 0) from Expenditure e "
        + "where e.memberId = :memberId and e.date between :start and :end")
    int findSumExpenditureByMemberIdAndBetween(@Param(value = "memberId") final Long memberId,
                                               @Param(value = "start") final LocalDateTime start,
                                               @Param(value = "end") final LocalDateTime end);
}
