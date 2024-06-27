package com.poorlex.poorlex.consumption.weeklybudget.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeeklyBudget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private WeeklyBudgetAmount amount;
    private Long memberId;

    public WeeklyBudget(final Long id,
                        final WeeklyBudgetAmount amount,
                        final Long memberId) {
        this.id = id;
        this.amount = amount;
        this.memberId = memberId;
    }

    public static WeeklyBudget withoutId(final WeeklyBudgetAmount amount,
                                         final Long memberId) {
        return new WeeklyBudget(null, amount, memberId);
    }

    public Long getId() {
        return id;
    }

    public Long getAmount() {
        return amount.getValue();
    }

    public void updateAmount(final WeeklyBudgetAmount amount) {
        this.amount = amount;
    }

    public Long getMemberId() {
        return memberId;
    }
}
