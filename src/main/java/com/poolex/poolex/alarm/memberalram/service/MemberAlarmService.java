package com.poolex.poolex.alarm.memberalram.service;

import com.poolex.poolex.alarm.memberalram.domain.MemberAlarm;
import com.poolex.poolex.alarm.memberalram.domain.MemberAlarmRepository;
import com.poolex.poolex.alarm.memberalram.service.dto.request.MemberAlarmRequest;
import com.poolex.poolex.alarm.memberalram.service.dto.response.MemberAlarmResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberAlarmService {

    private final MemberAlarmRepository memberAlarmRepository;
    private final MemberAlarmResponseConverter responseConverter;

    public List<MemberAlarmResponse> findMemberAlarms(final Long memberId, final MemberAlarmRequest request) {
        final List<MemberAlarm> memberAlarms = memberAlarmRepository.findAllByMemberId(memberId);
        return responseConverter.convert(memberAlarms, request.getDateTime());
    }
}
