package com.poorlex.poorlex.participate.service;

import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleRepository;
import com.poorlex.poorlex.battle.domain.BattleStatus;
import com.poorlex.poorlex.config.event.Events;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.participate.domain.BattleParticipant;
import com.poorlex.poorlex.participate.domain.BattleParticipantRepository;
import com.poorlex.poorlex.participate.service.event.BattleParticipantAddedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BattleParticipantService {

    private static final int MAX_READIED_BATTLE_COUNT = 3;

    private final MemberRepository memberRepository;
    private final BattleRepository battleRepository;
    private final BattleParticipantRepository battleParticipantRepository;

    @Transactional
    public Long participate(final Long battleId, final Long memberId) {
        validateMemberExist(memberId);
        validateMemberCanParticipateBattle(memberId);
        validateBattle(battleId);
        final BattleParticipant battleParticipant = BattleParticipant.normalPlayer(battleId, memberId);
        final BattleParticipant savedBattleParticipant = battleParticipantRepository.save(battleParticipant);
        Events.raise(new BattleParticipantAddedEvent(battleId));

        return savedBattleParticipant.getId();
    }

    private void validateMemberExist(final Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new IllegalArgumentException();
        }
    }

    private void validateMemberCanParticipateBattle(final Long memberId) {
        final int readiedBattleCount = battleRepository.countMemberBattleWithStatuses(
            memberId,
            BattleStatus.getReadiedStatues()
        );
        if (readiedBattleCount >= MAX_READIED_BATTLE_COUNT) {
            throw new IllegalArgumentException("배틀은 최대 3개까지 참여할 수 있습니다.");
        }
    }

    private void validateBattle(final Long battleId) {
        final Battle battle = battleRepository.findById(battleId)
            .orElseThrow(IllegalArgumentException::new);
        final int battleParticipantSize = battleParticipantRepository.countBattleParticipantByBattleId(battleId);

        if (!battle.isRecruiting() || battle.hasLessOrEqualMaxParticipantSizeThen(battleParticipantSize)) {
            throw new IllegalArgumentException();
        }
    }

    @Transactional
    public void withdraw(final Long battleId, final Long memberId) {
        final BattleParticipant battleParticipant = battleParticipantRepository.findByBattleIdAndMemberId(
            battleId,
            memberId
        ).orElseThrow(IllegalArgumentException::new);
        validateBattleCanWithdraw(battleId);
        validateParticipantNotManager(battleParticipant);
        battleParticipantRepository.delete(battleParticipant);
    }

    private void validateBattleCanWithdraw(final Long battleId) {
        final Battle battle = battleRepository.findById(battleId)
            .orElseThrow(IllegalArgumentException::new);
        if (battle.hasSameStatus(BattleStatus.COMPLETE)) {
            throw new IllegalArgumentException();
        }
    }

    private void validateParticipantNotManager(final BattleParticipant battleParticipant) {
        if (battleParticipant.isManager()) {
            throw new IllegalArgumentException("배틀의 매니저는 배틀을 나갈 수 없습니다.");
        }
    }
}
