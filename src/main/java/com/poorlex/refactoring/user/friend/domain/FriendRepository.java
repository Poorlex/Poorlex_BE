package com.poorlex.refactoring.user.friend.domain;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Query(value = "select "
        + "CASE WHEN f.firstMemberId <> :memberId THEN f.firstMemberId ELSE f.secondMemberId END "
        + "from Friend f "
        + "where f.firstMemberId = :memberId or f.secondMemberId = :memberId")
    List<Long> findMembersFriendMemberId(final Long memberId);

    @Query(value = "select count(f) from Friend f "
        + "where f.firstMemberId = :memberId or f.secondMemberId = :memberId")
    int countFriendByMemberId(final Long memberId);

    @Query(value = "select "
        + "CASE WHEN f.firstMemberId <> :memberId THEN f.firstMemberId ELSE f.secondMemberId END "
        + "from Friend f "
        + "where f.firstMemberId = :memberId or f.secondMemberId = :memberId")
    List<Long> findMembersFriendMemberIdLimit(final Long memberId, final Pageable pageable);

    boolean existsByFirstMemberIdAndSecondMemberId(final Long firstMemberId, final Long secondMemberId);
}
