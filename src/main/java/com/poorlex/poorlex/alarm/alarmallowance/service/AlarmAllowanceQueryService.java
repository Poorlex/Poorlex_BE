package com.poorlex.poorlex.alarm.alarmallowance.service;

import com.poorlex.poorlex.alarm.alarmallowance.domain.AlarmAllowanceRepository;
import com.poorlex.poorlex.alarm.alarmallowance.service.dto.response.AlarmAllowanceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlarmAllowanceQueryService {

    private final AlarmAllowanceRepository alarmAllowanceRepository;

    public AlarmAllowanceResponse findAllowance(final Long memberId) {
        return alarmAllowanceRepository.findByMemberId(memberId)
                .map(AlarmAllowanceResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("멤버의 알람 허용 정보가 존재하지 않습니다."));
    }
}
