package com.poorlex.poorlex.alarm.alarmallowance.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmAllowanceRepository extends JpaRepository<AlarmAllowance, Long> {
    Optional<AlarmAllowance> findByMemberId(Long memberId);
}
