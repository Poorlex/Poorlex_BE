package com.poolex.poolex.alarm.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Alarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long battleId;

    private Long memberId;

    @Enumerated(value = EnumType.STRING)
    private AlarmType type;

    private LocalDateTime createdAt;

    public Alarm(final Long id,
                 final Long battleId,
                 final Long memberId,
                 final AlarmType type) {
        this.id = id;
        this.battleId = battleId;
        this.memberId = memberId;
        this.type = type;
    }

    public static Alarm withoutId(final Long battleId,
                                  final Long memberId,
                                  final AlarmType type) {
        return new Alarm(null, battleId, memberId, type);
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getBattleId() {
        return battleId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public AlarmType getType() {
        return type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
