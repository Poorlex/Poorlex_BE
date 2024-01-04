package com.poolex.poolex.alarmreaction.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlarmReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long alarmId;

    @Enumerated(value = EnumType.STRING)
    private AlarmReactionType type;

    @Embedded
    private AlarmReactionContent content;

    private AlarmReaction(final Long id,
                          final Long alarmId,
                          final AlarmReactionType type,
                          final AlarmReactionContent content) {
        this.id = id;
        this.alarmId = alarmId;
        this.type = type;
        this.content = content;
    }

    public static AlarmReaction withoutId(final Long alarmId,
                                          final AlarmReactionType type,
                                          final AlarmReactionContent content) {
        return new AlarmReaction(null, alarmId, type, content);
    }

    public static AlarmReaction praiseWithoutId(final Long alarmId, final AlarmReactionContent content) {
        return withoutId(alarmId, AlarmReactionType.PRAISE, content);
    }

    public static AlarmReaction scoldWithoutId(final Long alarmId, final AlarmReactionContent content) {
        return withoutId(alarmId, AlarmReactionType.SCOLD, content);
    }

    public Long getId() {
        return id;
    }

    public Long getAlarmId() {
        return alarmId;
    }

    public AlarmReactionType getType() {
        return type;
    }

    public String getContent() {
        return content.getValue();
    }
}
