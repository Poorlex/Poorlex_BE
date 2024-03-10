package com.poorlex.poorlex.weeklybudget.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeeklyBudget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private WeeklyBudgetAmount amount;
    @Embedded
    private WeeklyBudgetDuration duration;
    private Long memberId;

    public WeeklyBudget(final Long id,
                        final WeeklyBudgetAmount amount,
                        final WeeklyBudgetDuration duration,
                        final Long memberId) {
        this.id = id;
        this.amount = amount;
        this.duration = duration;
        this.memberId = memberId;
    }

    public static WeeklyBudget withoutId(final WeeklyBudgetAmount amount,
                                         final WeeklyBudgetDuration duration,
                                         final Long memberId) {
        return new WeeklyBudget(null, amount, duration, memberId);
    }

    public long getDDay(final LocalDateTime current) {
        return ChronoUnit.DAYS.between(current, duration.getEnd());
    }

    public Long getId() {
        return id;
    }

    public Long getAmount() {
        return amount.getValue();
    }

    public WeeklyBudgetDuration getDuration() {
        return duration;
    }

    public Long getMemberId() {
        return memberId;
    }
}
