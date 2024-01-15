package com.poolex.poolex.point.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberPointRepository extends JpaRepository<MemberPoint, Long> {

    @Query(value = "select coalesce(sum(p.point.value), 0) from MemberPoint p "
        + "where p.memberId = :memberId")
    int findSumByMemberId(@Param(value = "memberId") final Long memberId);

    Optional<MemberPoint> findFirstByMemberIdOrderByIdDesc(final Long memberId);

    @Query(value = "select m.id as memberId, coalesce(sum(mp.point.value), 0) as totalPoint "
        + "from Member m left join MemberPoint mp on m.id = mp.memberId "
        + "where m.id in :memberIds "
        + "group by m.id")
    List<MemberIdAndTotalPointDto> findTotalPointsByMemberIdIn(final List<Long> memberIds);
}
