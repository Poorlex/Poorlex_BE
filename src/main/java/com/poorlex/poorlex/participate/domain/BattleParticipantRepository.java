package com.poorlex.poorlex.participate.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BattleParticipantRepository extends JpaRepository<BattleParticipant, Long> {

    int countBattleParticipantByBattleId(final Long battleId);

    Optional<BattleParticipant> findByBattleIdAndMemberId(final Long battleId, final Long memberId);

    boolean existsByBattleIdAndMemberIdAndRole(final Long battleId,
                                               final Long memberId,
                                               final BattleParticipantRole role);

    boolean existsByBattleIdAndMemberId(final Long battleId, final Long memberId);

    List<BattleParticipant> findAllByBattleId(final Long battleId);

    List<BattleParticipant> findAllByMemberId(final Long memberId);
}
