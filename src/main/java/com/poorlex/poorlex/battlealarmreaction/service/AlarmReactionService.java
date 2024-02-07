package com.poorlex.poorlex.battlealarmreaction.service;

import com.poorlex.poorlex.alarm.battlealarm.domain.BattleAlarm;
import com.poorlex.poorlex.alarm.battlealarm.domain.BattleAlarmRepository;
import com.poorlex.poorlex.battlealarmreaction.domain.AlarmReaction;
import com.poorlex.poorlex.battlealarmreaction.domain.AlarmReactionContent;
import com.poorlex.poorlex.battlealarmreaction.domain.AlarmReactionRepository;
import com.poorlex.poorlex.battlealarmreaction.domain.AlarmReactionType;
import com.poorlex.poorlex.battlealarmreaction.service.dto.request.AlarmReactionCreateRequest;
import com.poorlex.poorlex.battlealarmreaction.service.dto.response.AlarmReactionResponse;
import com.poorlex.poorlex.battlealarmreaction.service.mapper.AlarmReactionResponseMapper;
import java.util.List;
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

    public List<AlarmReactionResponse> findBattleAlarmReactions(final Long battleId) {
        return alarmReactionRepository.findAllByBattleId(battleId).stream()
            .map(AlarmReactionResponseMapper::mapToResponse)
            .toList();
    }
}
