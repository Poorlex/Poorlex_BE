package com.poorlex.poorlex.alarm.alarmallowance.service.dto.response;

import com.poorlex.poorlex.alarm.alarmallowance.domain.AlarmAllowance;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AlarmAllowanceResponse {

    private final boolean isAllowExpenditureRequestAlarm;
    private final boolean isAllowBattleStatusAlarm;
    private final boolean isAllowBattleChatAlarm;
    private final boolean isAllowFriendAlarm;
    private final boolean isAllowBattleInvitationAlarm;

    public static AlarmAllowanceResponse from(final AlarmAllowance alarmAllowance) {
        return new AlarmAllowanceResponse(alarmAllowance.isAllowExpenditureRequestAlarm(),
                                          alarmAllowance.isAllowBattleStatusAlarm(),
                                          alarmAllowance.isAllowBattleChatAlarm(),
                                          alarmAllowance.isAllowFriendAlarm(),
                                          alarmAllowance.isAllowBattleInvitationAlarm());
    }
}
