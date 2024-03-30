package com.poorlex.poorlex.alarm.memberalram.service;

import com.poorlex.poorlex.alarm.alarmallowance.domain.AlarmAllowance;
import com.poorlex.poorlex.alarm.alarmallowance.domain.AlarmAllowanceRepository;
import com.poorlex.poorlex.alarm.memberalram.domain.MemberAlarm;
import com.poorlex.poorlex.alarm.memberalram.domain.MemberAlarmRepository;
import com.poorlex.poorlex.alarm.memberalram.domain.MemberAlarmType;
import com.poorlex.poorlex.alarm.memberalram.service.dto.response.MemberAlarmResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberAlarmService {

    private final MemberAlarmRepository memberAlarmRepository;
    private final AlarmAllowanceRepository alarmAllowanceRepository;
    private final MemberAlarmResponseConverter responseConverter;

    public List<MemberAlarmResponse> findMemberAlarmsByCurrentTime(final Long memberId) {
        return findMemberAlarms(memberId, LocalDateTime.now());
    }

    public List<MemberAlarmResponse> findMemberAlarms(final Long memberId, final LocalDateTime dateTime) {
        final String errorMessage = String.format("멤버의 알람 허용 정보가 존재하지 않습니다.( 멤버 ID : %d )", memberId);
        final AlarmAllowance alarmAllowance = alarmAllowanceRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException(errorMessage));

        final List<MemberAlarmType> allowedAlarmType = getAllowedAlarmType(alarmAllowance);

        final List<MemberAlarm> memberAlarms = memberAlarmRepository.findAllByMemberIdAndTypeIn(memberId,
                                                                                                allowedAlarmType);
        return responseConverter.convert(memberAlarms, dateTime);
    }

    private static List<MemberAlarmType> getAllowedAlarmType(final AlarmAllowance alarmAllowance) {
        final List<MemberAlarmType> allowedAlarmType = new ArrayList<>();

        if (alarmAllowance.isAllowBattleInvitationAlarm()) {
            allowedAlarmType.addAll(MemberAlarmType.BATTLE_INVITE_ALARM_TYPES);
        }
        if (alarmAllowance.isAllowFriendAlarm()) {
            allowedAlarmType.addAll(MemberAlarmType.FRIEND_ALARM_TYPES);
        }
        return allowedAlarmType;
    }
}
