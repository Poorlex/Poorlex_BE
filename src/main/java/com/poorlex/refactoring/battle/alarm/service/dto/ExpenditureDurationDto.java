package com.poorlex.refactoring.battle.alarm.service.dto;

import java.time.LocalDateTime;

public class ExpenditureDurationDto {

    private final LocalDateTime start;
    private final LocalDateTime end;

    public ExpenditureDurationDto(final LocalDateTime start, final LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }
}
