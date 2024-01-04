package com.poolex.poolex.alarm.service;

import com.poolex.poolex.alarm.domain.Alarm;
import com.poolex.poolex.alarm.domain.AlarmRepository;
import com.poolex.poolex.alarm.service.dto.request.BattleAlarmResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;

    public List<BattleAlarmResponse> findBattleAlarms(final Long battleId) {
        final List<Alarm> battleAlarms = alarmRepository.findAllByBattleId(battleId);
        
        return BattleAlarmResponse.mapToList(battleAlarms);
    }
}
