package com.poorlex.poorlex.consumption.goal.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    @Query(value = "select g.id from Goal g where g.memberId = :memberId")
    List<Long> findIdsByMemberId(final Long memberId);

    List<Goal> findAllByMemberIdAndStatus(final Long memberId, final GoalStatus status);
}
