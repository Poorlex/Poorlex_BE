package com.poorlex.poorlex.alarm.alarmallowance.service;

import com.poorlex.poorlex.alarm.alarmallowance.domain.AlarmAllowance;
import com.poorlex.poorlex.alarm.alarmallowance.domain.AlarmAllowanceRepository;
import com.poorlex.poorlex.alarm.alarmallowance.domain.AlarmAllowanceType;
import com.poorlex.poorlex.alarm.alarmallowance.service.dto.request.AlarmAllowanceStatusChangeRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AlarmAllowanceCommandService {

    private final AlarmAllowanceRepository alarmAllowanceRepository;

    public void changeAlarmAllowanceStatus(final Long memberId, final AlarmAllowanceStatusChangeRequest request) {
        final AlarmAllowance alarmAllowance = alarmAllowanceRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("알림 허용 여부를 변경하려는 회원이 존재하지 않습니다."));

        final String alarmType = request.getAlarmType();
        final AlarmAllowanceType alarmAllowanceType = AlarmAllowanceType.findByName(alarmType)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("알림 카테고리가 존재하지 않습니다. ( 입력 : %s )", alarmType))
                );
        alarmAllowance.update(alarmAllowanceType, request.isAllowed());
    }
}
