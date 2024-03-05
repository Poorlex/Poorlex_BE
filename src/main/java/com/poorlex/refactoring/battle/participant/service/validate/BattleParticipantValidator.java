package com.poorlex.refactoring.battle.participant.service.validate;

import com.poorlex.refactoring.battle.battle.domain.Battle;
import com.poorlex.refactoring.battle.battle.domain.BattleRepository;
import com.poorlex.refactoring.battle.battle.domain.BattleStatus;
import com.poorlex.refactoring.battle.participant.domain.BattleParticipant;
import com.poorlex.refactoring.battle.participant.domain.BattleParticipantRepository;
import com.poorlex.refactoring.battle.participant.service.validate.exception.BattleNotExistException;
import com.poorlex.refactoring.battle.participant.service.validate.exception.BattleNotPartcipatableException;
import com.poorlex.refactoring.battle.participant.service.validate.exception.BattleNotWithdrawableException;
import com.poorlex.refactoring.battle.participant.service.validate.exception.BattleParticipantNotExistException;
import com.poorlex.refactoring.battle.participant.service.validate.exception.ManagerCanNotWithdrawBattleException;
import com.poorlex.refactoring.battle.participant.service.validate.exception.MaxBattleSizeException;
import com.poorlex.refactoring.battle.participant.service.validate.exception.MemberNotExistException;
import com.poorlex.refactoring.user.member.domain.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BattleParticipantValidator {

    private final int maxBattleCount;
    private final MemberRepository memberRepository;
    private final BattleRepository battleRepository;
    private final BattleParticipantRepository battleParticipantRepository;

    public BattleParticipantValidator(@Value("${battle.max-battle-count}") final int maxBattleCount,
                                      final MemberRepository memberRepository,
                                      final BattleRepository battleRepository,
                                      final BattleParticipantRepository battleParticipantRepository) {
        this.maxBattleCount = maxBattleCount;
        this.memberRepository = memberRepository;
        this.battleRepository = battleRepository;
        this.battleParticipantRepository = battleParticipantRepository;
    }

    public void memberCanParticipateBattle(final Long memberId, final Long battleId) {
        memberExist(memberId);
        memberParticipatingBattleLessThanMaxBattleCount(memberId);
        isBattleParticipatable(battleId);
    }

    private void memberExist(final Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotExistException();
        }
    }

    public void memberParticipatingBattleLessThanMaxBattleCount(final Long memberId) {
        final int readiedBattleCount = battleRepository.countMemberBattleWithStatuses(
            memberId,
            BattleStatus.getReadyStatues()
        );
        if (readiedBattleCount >= maxBattleCount) {
            throw new MaxBattleSizeException();
        }
    }

    private void isBattleParticipatable(final Long battleId) {
        final Battle battle = battleRepository.findById(battleId)
            .orElseThrow(BattleNotExistException::new);

        if (!battle.isRecruiting()) {
            throw new BattleNotPartcipatableException();
        }
    }

    public void participantCanWithdrawBattle(final Long memberId, final Long battleId) {
        isBattleWithdrawable(battleId);
        isPartcipantWithdrawable(memberId, battleId);
    }

    private void isBattleWithdrawable(final Long battleId) {
        final Battle battle = battleRepository.findById(battleId)
            .orElseThrow(BattleNotExistException::new);

        if (battle.isProgressing()) {
            throw new BattleNotWithdrawableException();
        }
    }

    private void isPartcipantWithdrawable(final Long memberId, final Long battleId) {
        final BattleParticipant battleParticipant =
            battleParticipantRepository.findByBattleIdAndMemberId(battleId, memberId)
                .orElseThrow(BattleParticipantNotExistException::new);

        if (battleParticipant.isManager()) {
            throw new ManagerCanNotWithdrawBattleException();
        }
    }
}
