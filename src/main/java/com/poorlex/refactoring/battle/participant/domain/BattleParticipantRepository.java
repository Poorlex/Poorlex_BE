package com.poorlex.refactoring.battle.participant.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BattleParticipantRepository extends JpaRepository<BattleParticipant, Long> {

    int countBattleParticipantByBattleId(final Long battleId);

    boolean existsByBattleIdAndMemberId(final Long battleId, final Long memberId);

    Optional<BattleParticipant> findByBattleIdAndMemberId(final Long battleId, final Long memberId);

    List<BattleParticipant> findByBattleId(final Long battleId);
}
