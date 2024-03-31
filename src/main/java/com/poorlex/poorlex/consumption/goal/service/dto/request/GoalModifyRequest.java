package com.poorlex.poorlex.consumption.goal.service.dto.request;

import java.time.LocalDate;

public interface GoalModifyRequest {

    String getType();

    String getName();

    int getAmount();

    LocalDate getStartDate();

    LocalDate getEndDate();
}
