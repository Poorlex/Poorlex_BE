package com.poolex.poolex.participate.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BattleParticipantRepository extends JpaRepository<BattleParticipant, Long> {

    int countBattleParticipantByBattleId(final Long battleId);

    boolean existsByBattleIdAndMemberIdAndRole(final Long battleId,
                                               final Long memberId,
                                               final BattleParticipantRole role);
}
