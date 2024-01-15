package com.poolex.poolex.member.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsById(final Long id);

    Optional<Member> findByOauthId(final String oauthId);
}
