package com.poorlex.poorlex.goal.service.dto.request;

import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GoalCreateRequest {

    private final String type;
    private final String name;
    private final int amount;
    private final LocalDate startDate;
    private final LocalDate endDate;
}
