package com.poorlex.poorlex.consumption.weeklybudget.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeeklyBudgetRepository extends JpaRepository<WeeklyBudget, Long> {

    List<WeeklyBudget> findWeeklyBudgetsByMemberId(final Long memberId);

    Optional<WeeklyBudget> findByMemberId(Long memberId);
}
