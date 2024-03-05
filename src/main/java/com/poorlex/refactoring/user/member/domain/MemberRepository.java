package com.poorlex.refactoring.user.member.domain;

import com.poorlex.poorlex.member.domain.MemberIdAndNicknameDto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsById(final Long id);

    Optional<Member> findByOauth2RegistrationIdAndOauthId(
        final Oauth2RegistrationId registrationId,
        final String oauthId);

    Optional<Member> findByOauthId(final String oauthId);

    @Query(value = "select m.id as memberId, m.nickname.value as nickname "
        + "from Member m "
        + "where m.id in :memberIds")
    List<MemberIdAndNicknameDto> getMemberNicknamesByMemberIds(final List<Long> memberIds);

    @Query(value = "select m.nickname.value from Member m where m.id = :memberId")
    String findMemberNicknameByMemberId(final Long memberId);
}
