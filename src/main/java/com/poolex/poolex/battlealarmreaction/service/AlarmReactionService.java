package com.poolex.poolex.battlealarmreaction.service;

import com.poolex.poolex.alarm.battlealarm.domain.BattleAlarm;
import com.poolex.poolex.alarm.battlealarm.domain.BattleAlarmRepository;
import com.poolex.poolex.battlealarmreaction.domain.AlarmReaction;
import com.poolex.poolex.battlealarmreaction.domain.AlarmReactionContent;
import com.poolex.poolex.battlealarmreaction.domain.AlarmReactionRepository;
import com.poolex.poolex.battlealarmreaction.domain.AlarmReactionType;
import com.poolex.poolex.battlealarmreaction.service.dto.request.AlarmReactionCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlarmReactionService {

    private final AlarmReactionRepository alarmReactionRepository;
    private final BattleAlarmRepository battleAlarmRepository;

    @Transactional
    public void createAlarmReaction(final Long memberId, final AlarmReactionCreateRequest request) {
        validateReactableAlarmType(request.getAlarmId());
        final AlarmReaction alarmReaction = mapToAlarmReaction(memberId, request);
        alarmReactionRepository.save(alarmReaction);
    }

    private AlarmReaction mapToAlarmReaction(final Long memberId, final AlarmReactionCreateRequest request) {
        final Long alarmId = request.getAlarmId();
        final AlarmReactionType alarmReactionType = AlarmReactionType.findByName(request.getType());
        final AlarmReactionContent content = new AlarmReactionContent(request.getContent());

        return AlarmReaction.withoutId(alarmId, memberId, alarmReactionType, content);
    }

    private void validateReactableAlarmType(final Long alarmId) {
        final BattleAlarm battleAlarm = battleAlarmRepository.findById(alarmId)
            .orElseThrow(IllegalArgumentException::new);

        if (!battleAlarm.isReactable()) {
            throw new IllegalArgumentException();
        }
    }
}
