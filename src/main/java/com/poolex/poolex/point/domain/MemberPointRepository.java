package com.poolex.poolex.point.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberPointRepository extends JpaRepository<MemberPoint, Long> {

    @Query(value = "select coalesce(sum(p.point.value), 0) from MemberPoint p "
        + "where p.memberId = :memberId")
    int findSumByMemberId(@Param(value = "memberId") final Long memberId);
}
