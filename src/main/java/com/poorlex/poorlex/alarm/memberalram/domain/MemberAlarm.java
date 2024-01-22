package com.poorlex.poorlex.alarm.memberalram.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberAlarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;
    private Long targetId;

    @Enumerated(value = EnumType.STRING)
    private MemberAlarmType type;

    private LocalDateTime createdAt;

    public MemberAlarm(final Long id, final Long memberId, final Long targetId, final MemberAlarmType type) {
        this.id = id;
        this.memberId = memberId;
        this.targetId = targetId;
        this.type = type;
    }

    public static MemberAlarm withoutId(final Long memberId, final Long targetId, final MemberAlarmType type) {
        return new MemberAlarm(null, memberId, targetId, type);
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public void updateType(final MemberAlarmType updateType) {
        this.type = updateType;
    }

    public long getMinutePassed(final LocalDateTime dateTime) {
        return ChronoUnit.MINUTES.between(createdAt, dateTime);
    }

    public long getHourPassed(final LocalDateTime dateTime) {
        return ChronoUnit.HOURS.between(createdAt, dateTime);

    }

    public long getDayPassed(final LocalDateTime dateTime) {
        return ChronoUnit.DAYS.between(createdAt, dateTime);
    }

    public Long getId() {
        return id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public Long getTargetId() {
        return targetId;
    }

    public MemberAlarmType getType() {
        return type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
