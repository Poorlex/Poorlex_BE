package com.poolex.poolex.participate.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BattleParticipantRepository extends JpaRepository<BattleParticipant, Long> {

    int countBattleParticipantByBattleId(final Long battleId);

    Optional<BattleParticipant> findByBattleIdAndMemberId(final Long battleId, final Long memberId);

    boolean existsByBattleIdAndMemberIdAndRole(final Long battleId,
                                               final Long memberId,
                                               final BattleParticipantRole role);
}
