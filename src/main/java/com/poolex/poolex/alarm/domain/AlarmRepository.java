package com.poolex.poolex.alarm.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    List<Alarm> findByType(final AlarmType type);
}
