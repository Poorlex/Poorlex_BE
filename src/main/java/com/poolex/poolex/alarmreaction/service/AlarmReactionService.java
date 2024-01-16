package com.poolex.poolex.alarmreaction.service;

import com.poolex.poolex.alarm.domain.Alarm;
import com.poolex.poolex.alarm.domain.AlarmRepository;
import com.poolex.poolex.alarmreaction.domain.AlarmReaction;
import com.poolex.poolex.alarmreaction.domain.AlarmReactionContent;
import com.poolex.poolex.alarmreaction.domain.AlarmReactionRepository;
import com.poolex.poolex.alarmreaction.domain.AlarmReactionType;
import com.poolex.poolex.alarmreaction.service.dto.request.AlarmReactionCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlarmReactionService {

    private final AlarmReactionRepository alarmReactionRepository;
    private final AlarmRepository alarmRepository;

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
        final Alarm alarm = alarmRepository.findById(alarmId)
            .orElseThrow(IllegalArgumentException::new);

        if (!alarm.isReactable()) {
            throw new IllegalArgumentException();
        }
    }
}
