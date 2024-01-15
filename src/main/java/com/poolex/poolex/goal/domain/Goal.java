package com.poolex.poolex.goal.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long memberId;
    @Enumerated(value = EnumType.STRING)
    private GoalType type;
    @Embedded
    private GoalName name;
    @Embedded
    private GoalAmount amount;
    @Embedded
    private GoalDuration duration;
    @Enumerated(value = EnumType.STRING)
    private GoalStatus status;

    public Goal(final Long id,
                final Long memberId,
                final GoalType type,
                final GoalName name,
                final GoalAmount amount,
                final GoalDuration duration,
                final GoalStatus status) {
        this.id = id;
        this.memberId = memberId;
        this.type = type;
        this.name = name;
        this.amount = amount;
        this.duration = duration;
        this.status = status;
    }

    public static Goal withoutId(final Long memberId,
                                 final GoalType type,
                                 final GoalName name,
                                 final GoalAmount amount,
                                 final GoalDuration duration,
                                 final GoalStatus status) {
        return new Goal(null, memberId, type, name, amount, duration, status);
    }

    public Long getId() {
        return id;
    }

    public GoalType getType() {
        return type;
    }

    public String getName() {
        return name.getValue();
    }

    public long getAmount() {
        return amount.getValue();
    }

    public LocalDate getStart() {
        return duration.getStart();
    }

    public LocalDate getEnd() {
        return duration.getEnd();
    }

    public GoalDurationType getDurationType() {
        return duration.getType();
    }

    public GoalStatus getStatus() {
        return status;
    }
}
