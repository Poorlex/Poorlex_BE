package com.poorlex.poorlex.participate.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BattleParticipantRepository extends JpaRepository<BattleParticipant, Long> {

    int countBattleParticipantByBattleId(final Long battleId);

    Optional<BattleParticipant> findByBattleIdAndMemberId(final Long battleId, final Long memberId);

    boolean existsByBattleIdAndMemberIdAndRole(final Long battleId,
                                               final Long memberId,
                                               final BattleParticipantRole role);

    boolean existsByBattleIdAndMemberId(final Long battleId, final Long memberId);

    List<BattleParticipant> findAllByBattleId(final Long battleId);
}
