package com.poorlex.poorlex.voting.vote.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long battleId;

    private Long memberId;

    @Embedded
    private VoteAmount amount;
    @Embedded
    private VoteDuration duration;
    @Embedded
    private VoteName name;
    @Enumerated(EnumType.STRING)
    private VoteStatus status;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }

    public Vote(final Long id,
                final Long battleId,
                final Long memberId,
                final VoteAmount amount,
                final VoteDuration duration,
                final VoteName name,
                final VoteStatus status) {
        this.id = id;
        this.battleId = battleId;
        this.memberId = memberId;
        this.amount = amount;
        this.duration = duration;
        this.name = name;
        this.status = status;
    }

    public static Vote withoutId(final Long battleId,
                                 final Long memberId,
                                 final VoteAmount amount,
                                 final VoteDuration duration,
                                 final VoteName name,
                                 final VoteStatus status) {
        return new Vote(null, battleId, memberId, amount, duration, name, status);
    }

    public boolean isFinished() {
        return status == VoteStatus.FINISHED;
    }

    public Long getId() {
        return id;
    }

    public Long getBattleId() {
        return battleId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public long getAmount() {
        return amount.getValue();
    }

    public LocalDateTime getStart() {
        return duration.getStart();
    }

    public String getName() {
        return name.getValue();
    }

    public VoteStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
