package com.poorlex.poorlex.weeklybudget.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WeeklyBudgetRepository extends JpaRepository<WeeklyBudget, Long> {

    @Query(value = "select w from WeeklyBudget w "
            + "where w.memberId = :memberId and :current between w.duration.start and w.duration.end")
    Optional<WeeklyBudget> findByMemberIdAndCurrentDate(@Param(value = "memberId") final Long memberId,
                                                        @Param(value = "current") final LocalDate current);

    List<WeeklyBudget> findWeeklyBudgetsByMemberId(final Long memberId);
}
