package com.poorlex.poorlex.goal.service.dto.response;

import com.poorlex.poorlex.goal.domain.Goal;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class GoalResponse {

    private final Long id;
    private final String name;
    private final String durationType;
    private final long amount;
    private final long dayLeft;
    private final long monthLeft;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public GoalResponse(final Long id,
                        final String name,
                        final String durationType,
                        final long amount,
                        final long dayLeft,
                        final long monthLeft,
                        final LocalDate startDate,
                        final LocalDate endDate) {
        this.id = id;
        this.name = name;
        this.durationType = durationType;
        this.amount = amount;
        this.dayLeft = dayLeft;
        this.monthLeft = monthLeft;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static GoalResponse fromProgressGoal(final Goal goal, final LocalDate date) {
        return new GoalResponse(
            goal.getId(),
            goal.getName(),
            goal.getDurationType().name(),
            goal.getAmount(),
            goal.getDayLeft(date),
            goal.getMonthLeft(date),
            goal.getStartDate(),
            goal.getEndDate()
        );
    }

    public static GoalResponse fromFinishGoal(final Goal goal) {
        return new GoalResponse(
            goal.getId(),
            goal.getName(),
            goal.getDurationType().name(),
            goal.getAmount(),
            0,
            0,
            goal.getStartDate(),
            goal.getEndDate()
        );
    }
}
