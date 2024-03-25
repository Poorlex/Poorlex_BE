package com.poorlex.poorlex.participate.service;

import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleRepository;
import com.poorlex.poorlex.battle.domain.BattleStatus;
import com.poorlex.poorlex.config.event.Events;
import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.exception.ExceptionTag;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
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
            final String errorMessage = String.format("Id 에 해당하는 회원이 존재하지 않습니다. ( ID : %d )", memberId);
            throw new ApiException(ExceptionTag.MEMBER_FIND, errorMessage);
        }
    }

    private void validateMemberCanParticipateBattle(final Long memberId) {
        final int readiedBattleCount = battleRepository.countMemberBattleWithStatuses(
                memberId,
                BattleStatus.getReadiedStatues()
        );
        if (readiedBattleCount >= MAX_READIED_BATTLE_COUNT) {
            final String errorMessage = String.format("배틀은 최대 %d개까지 참여할 수 있습니다. ( 참가 중인 배틀 수 : %d )",
                                                      MAX_READIED_BATTLE_COUNT,
                                                      readiedBattleCount);
            throw new ApiException(ExceptionTag.BATTLE_PARTICIPATE, errorMessage);
        }
    }

    private void validateBattle(final Long battleId) {
        final Battle battle = battleRepository.findById(battleId)
                .orElseThrow(() -> {
                    final String errorMessage = String.format("ID에 해당하는 배틀이 존재하지 않습니다. ( ID : %d )", battleId);
                    return new ApiException(ExceptionTag.BATTLE_FIND, errorMessage);
                });

        if (!battle.isRecruiting()) {
            final String errorMessage = String.format("참가하려는 배틀이 모집중이 아닙니다. ( 배틀 Id : %d )", battleId);
            throw new ApiException(ExceptionTag.BATTLE_PROGRESS, errorMessage);
        }
    }

    @Transactional
    public void withdraw(final Long battleId, final Long memberId) {
        final BattleParticipant battleParticipant =
                battleParticipantRepository.findByBattleIdAndMemberId(battleId, memberId)
                        .orElseThrow(() -> {
                            final String errorMessage = String.format("배틀 참가자가 존재하지 않습니다. ( 배틀 Id : %d , 회원 Id : %d )",
                                                                      battleId,
                                                                      memberId);
                            return new ApiException(ExceptionTag.BATTLE_PARTICIPANT_FIND, errorMessage);
                        });

        validateBattleCanWithdraw(battleId);
        validateParticipantNotManager(battleParticipant);
        battleParticipantRepository.delete(battleParticipant);
    }

    private void validateBattleCanWithdraw(final Long battleId) {
        final Battle battle = battleRepository.findById(battleId)
                .orElseThrow(IllegalArgumentException::new);
        if (battle.hasSameStatus(BattleStatus.COMPLETE)) {
            final String errorMessage = String.format("탈퇴하려는 배틀이 이미 완료된 배틀입니다. ( 배틀 Id : %d )", battleId);
            throw new ApiException(ExceptionTag.BATTLE_PROGRESS, errorMessage);
        }
    }

    private void validateParticipantNotManager(final BattleParticipant battleParticipant) {
        if (battleParticipant.isManager()) {
            throw new ApiException(ExceptionTag.BATTLE_WITHDRAW, "배틀의 매니저는 배틀을 나갈 수 없습니다.");
        }
    }
}
