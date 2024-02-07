package com.poorlex.poorlex.voting.votingpaper.domain;

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
public class VotingPaper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long voteId;
    private Long voterMemberId;
    @Enumerated(EnumType.STRING)
    private VotingPaperType type;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }

    public VotingPaper(final Long id, final Long voteId, final Long voterMemberId, final VotingPaperType type) {
        this.id = id;
        this.voteId = voteId;
        this.voterMemberId = voterMemberId;
        this.type = type;
    }

    public static VotingPaper withoutId(final Long voteId, final Long voterMemberId, final VotingPaperType type) {
        return new VotingPaper(null, voteId, voterMemberId, type);
    }

    public boolean isAgree() {
        return type == VotingPaperType.AGREE;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
