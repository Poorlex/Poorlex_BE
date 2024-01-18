package com.poolex.poolex.alarm.battlealarm.service;

import com.poolex.poolex.alarm.battlealarm.domain.BattleAlarm;
import com.poolex.poolex.alarm.battlealarm.domain.BattleAlarmRepository;
import com.poolex.poolex.alarm.battlealarm.service.dto.response.BattleAlarmResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BattleAlarmService {

    private final BattleAlarmRepository battleAlarmRepository;

    public List<BattleAlarmResponse> findBattleAlarms(final Long battleId) {
        final List<BattleAlarm> battleBattleAlarms = battleAlarmRepository.findAllByBattleId(battleId);

        return BattleAlarmResponse.generateListBy(battleBattleAlarms);
    }
}
