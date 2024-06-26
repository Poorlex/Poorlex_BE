package com.poorlex.poorlex.consumption.goal.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    @Query(value = "select g.id from Goal g where g.memberId = :memberId")
    List<Long> findIdsByMemberId(@Param("memberId") final Long memberId);

    List<Goal> findAllByMemberIdAndStatus(final Long memberId, final GoalStatus status);
}
