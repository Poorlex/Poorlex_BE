package com.poorlex.poorlex.consumption.goal.service.dto.request;

import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberGoalRequest {

    private final String status;
    private final LocalDate date;
}
