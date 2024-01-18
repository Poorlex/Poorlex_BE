package com.poolex.poolex.battlealarmreaction.domain;

import jakarta.persistence.Embedded;
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
public class BattleAlarmReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long alarmId;

    private Long memberId;

    @Enumerated(value = EnumType.STRING)
    private BattleAlarmReactionType type;

    @Embedded
    private BattleAlarmReactionContent content;

    private LocalDateTime createdAt;

    private BattleAlarmReaction(final Long id,
                                final Long alarmId,
                                final Long memberId,
                                final BattleAlarmReactionType type,
                                final BattleAlarmReactionContent content) {
        this.id = id;
        this.alarmId = alarmId;
        this.memberId = memberId;
        this.type = type;
        this.content = content;
    }

    public static BattleAlarmReaction withoutId(final Long alarmId,
                                                final Long memberId,
                                                final BattleAlarmReactionType type,
                                                final BattleAlarmReactionContent content) {
        return new BattleAlarmReaction(null, alarmId, memberId, type, content);
    }

    public static BattleAlarmReaction praiseWithoutId(final Long alarmId,
                                                      final Long memberId,
                                                      final BattleAlarmReactionContent content) {
        return withoutId(alarmId, memberId, BattleAlarmReactionType.PRAISE, content);
    }

    public static BattleAlarmReaction scoldWithoutId(final Long alarmId,
                                                     final Long memberId,
                                                     final BattleAlarmReactionContent content) {
        return withoutId(alarmId, memberId, BattleAlarmReactionType.SCOLD, content);
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getAlarmId() {
        return alarmId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public BattleAlarmReactionType getType() {
        return type;
    }

    public BattleAlarmReactionContent getContent() {
        return content;
    }
}
