package com.poorlex.poorlex.goal.service.dto.response;

import com.poorlex.poorlex.goal.domain.GoalType;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GoalTypeResponse {

    private final String typeName;
    private final List<String> recommendGoalNames;

    public static GoalTypeResponse from(final GoalType goalType) {
        return new GoalTypeResponse(goalType.getName(), goalType.getRecommendNames());
    }
}
