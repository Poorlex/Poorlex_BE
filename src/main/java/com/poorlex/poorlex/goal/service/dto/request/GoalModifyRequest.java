package com.poorlex.poorlex.goal.service.dto.request;

import java.time.LocalDate;

public interface GoalModifyRequest {

    String getType();

    String getName();

    int getAmount();

    LocalDate getStartDate();

    LocalDate getEndDate();
}
