package com.poorlex.poorlex.friend.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Query(value = "select "
            + "CASE WHEN f.firstMemberId <> :memberId THEN f.firstMemberId ELSE f.secondMemberId END "
            + "from Friend f "
            + "where f.firstMemberId = :memberId or f.secondMemberId = :memberId")
    List<Long> findFriendIdsByMemberId(@Param("memberId") final Long memberId);

    boolean existsByFirstMemberIdAndSecondMemberId(final Long firstMemberId, final Long secondMemberId);

    @Query(value = "select f from Friend f " +
            "where f.firstMemberId = :memberId or f.secondMemberId = :memberId")
    List<Friend> findFriendsByMemberId(@Param("memberId") final Long memberId);
}
