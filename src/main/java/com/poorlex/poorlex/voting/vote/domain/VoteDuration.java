package com.poorlex.poorlex.voting.vote.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoteDuration {

    @Column(name = "start")
    private LocalDateTime start;
    @Column(name = "type")
    @Enumerated(value = EnumType.STRING)
    private VoteDurationType type;

    public VoteDuration(final LocalDateTime start, final VoteDurationType type) {
        this.start = start;
        this.type = type;
    }

    public boolean isEnd(final LocalDateTime time) {
        return time.isAfter(type.getEnd(start));
    }

    public LocalDateTime getStart() {
        return start;
    }

    @Column(name = "end")
    public LocalDateTime getEnd() {
        return type.getEnd(start);
    }

    public VoteDurationType getType() {
        return type;
    }
}
