package com.poorlex.poorlex.voting.vote.domain;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

public enum VoteDurationType {
    FIVE_MINUTE(5),
    TEN_MINUTE(10),
    TWENTY_MINUTE(20),
    THIRTY_MINUTE(30),
    SIXTY_MINUTE(60);

    private final int minutes;

    VoteDurationType(final int minutes) {
        this.minutes = minutes;
    }

    public static Optional<VoteDurationType> findByMinute(final int targetMinute) {
        return Arrays.stream(values())
            .filter(type -> type.minutes == targetMinute)
            .findFirst();
    }

    public LocalDateTime getEnd(final LocalDateTime start) {
        return start.plusMinutes(this.minutes);
    }
}
