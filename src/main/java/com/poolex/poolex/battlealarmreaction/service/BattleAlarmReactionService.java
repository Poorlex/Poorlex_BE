package com.poolex.poolex.battlealarmreaction.service;

import com.poolex.poolex.alarm.battlealarm.domain.BattleAlarm;
import com.poolex.poolex.alarm.battlealarm.domain.BattleAlarmRepository;
import com.poolex.poolex.battlealarmreaction.domain.BattleAlarmReaction;
import com.poolex.poolex.battlealarmreaction.domain.BattleAlarmReactionContent;
import com.poolex.poolex.battlealarmreaction.domain.BattleAlarmReactionRepository;
import com.poolex.poolex.battlealarmreaction.domain.BattleAlarmReactionType;
import com.poolex.poolex.battlealarmreaction.service.dto.request.BattleAlarmReactionCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BattleAlarmReactionService {

    private final BattleAlarmReactionRepository battleAlarmReactionRepository;
    private final BattleAlarmRepository battleAlarmRepository;

    @Transactional
    public void createAlarmReaction(final Long memberId, final BattleAlarmReactionCreateRequest request) {
        validateReactableAlarmType(request.getAlarmId());
        final BattleAlarmReaction battleAlarmReaction = mapToAlarmReaction(memberId, request);
        battleAlarmReactionRepository.save(battleAlarmReaction);
    }

    private BattleAlarmReaction mapToAlarmReaction(final Long memberId,
                                                   final BattleAlarmReactionCreateRequest request) {
        final Long alarmId = request.getAlarmId();
        final BattleAlarmReactionType battleAlarmReactionType = BattleAlarmReactionType.findByName(request.getType());
        final BattleAlarmReactionContent content = new BattleAlarmReactionContent(request.getContent());

        return BattleAlarmReaction.withoutId(alarmId, memberId, battleAlarmReactionType, content);
    }

    private void validateReactableAlarmType(final Long alarmId) {
        final BattleAlarm battleAlarm = battleAlarmRepository.findById(alarmId)
            .orElseThrow(IllegalArgumentException::new);

        if (!battleAlarm.isReactable()) {
            throw new IllegalArgumentException();
        }
    }
}
