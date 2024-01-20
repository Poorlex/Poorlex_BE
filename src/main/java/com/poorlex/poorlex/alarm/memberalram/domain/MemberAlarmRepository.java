package com.poorlex.poorlex.alarm.memberalram.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberAlarmRepository extends JpaRepository<MemberAlarm, Long> {

    List<MemberAlarm> findAllByMemberId(final Long memberId);
}
