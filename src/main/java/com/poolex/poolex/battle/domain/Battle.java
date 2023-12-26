package com.poolex.poolex.battle.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Battle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private BattleName name;
    @Embedded
    private BattleIntroduction introduction;
    @Embedded
    private BattleImageUrl imageUrl;
    @Embedded
    private BattleBudget budget;
    @Embedded
    private BattleParticipantSize maxParticipantSize;
    @Embedded
    private BattleDuration duration;
    @Enumerated(value = EnumType.STRING)
    private BattleStatus status;

    public Battle(final Long id,
                  @NonNull final BattleName name,
                  @NonNull final BattleIntroduction introduction,
                  @NonNull final BattleImageUrl imageUrl,
                  @NonNull final BattleBudget budget,
                  @NonNull final BattleParticipantSize maxParticipantSize,
                  @NonNull final BattleDuration duration,
                  @NonNull final BattleStatus status) {
        this.id = id;
        this.name = name;
        this.introduction = introduction;
        this.imageUrl = imageUrl;
        this.budget = budget;
        this.maxParticipantSize = maxParticipantSize;
        this.duration = duration;
        this.status = status;
    }

    public static Battle withoutBattleId(final BattleName name,
                                         final BattleIntroduction introduction,
                                         final BattleImageUrl imageUrl,
                                         final BattleBudget budget,
                                         final BattleParticipantSize maxParticipantSize,
                                         final BattleDuration duration,
                                         final BattleStatus status) {
        return new Battle(null, name, introduction, imageUrl, budget, maxParticipantSize, duration, status);
    }

    public void start(final LocalDateTime dateTime) {
        //스케쥴링으로 실행해야 함
        if (!isTimeAbleToStart(dateTime) || !isStatusAbleToStart()) {
            throw new IllegalArgumentException();
        }
        changeStatus(BattleStatus.PROGRESS);
    }

    private boolean isTimeAbleToStart(final LocalDateTime dateTime) {
        final long subtractHours = ChronoUnit.HOURS.between(dateTime, duration.getStart());
        return subtractHours == 0;
    }

    private boolean isStatusAbleToStart() {
        return hasSameStatus(BattleStatus.RECRUITING) || hasSameStatus(BattleStatus.RECRUITING_FINISHED);
    }

    private void changeStatus(final BattleStatus status) {
        this.status = status;
    }

    public void end(final LocalDateTime dateTime) {
        //스케쥴링으로 시작해야함
        if (!isTimeAbleToEnd(dateTime) || !isStatusAbleToEnd()) {
            throw new IllegalArgumentException();
        }
        changeStatus(BattleStatus.COMPLETE);
    }

    private boolean isTimeAbleToEnd(final LocalDateTime dateTime) {
        final long subtractHours = ChronoUnit.HOURS.between(dateTime, duration.getEnd());
        return subtractHours == 0;
    }

    private boolean isStatusAbleToEnd() {
        return hasSameStatus(BattleStatus.PROGRESS);
    }

    public boolean hasSameStatus(final BattleStatus targetStatus) {
        return this.status == targetStatus;
    }

    public boolean isRecruiting() {
        return hasSameStatus(BattleStatus.RECRUITING);
    }

    public void recruit() {
        status = BattleStatus.RECRUITING;
    }

    public void recruitFinish() {
        status = BattleStatus.RECRUITING_FINISHED;
    }

    public boolean hasLessOrEqualMaxParticipantSizeThen(final int targetSize) {
        return !maxParticipantSize.hasGreaterValue(targetSize);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getValue();
    }

    public String getIntroduction() {
        return introduction.getValue();
    }

    public String getImageUrl() {
        return imageUrl.getValue();
    }

    public int getBudget() {
        return budget.getValue();
    }

    public BattleParticipantSize getMaxParticipantSize() {
        return maxParticipantSize;
    }

    public BattleDuration getDuration() {
        return duration;
    }

    public BattleStatus getStatus() {
        return status;
    }

    public BattleDifficulty getDifficulty() {
        return budget.getDifficulty();
    }
}
