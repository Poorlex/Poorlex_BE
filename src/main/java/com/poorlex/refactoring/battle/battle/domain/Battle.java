package com.poorlex.refactoring.battle.battle.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
    private BattleParticipantLimit battleParticipantLimit;
    @Embedded
    private BattleDuration duration;
    @Enumerated(value = EnumType.STRING)
    private BattleStatus status;
    private boolean isBattleSuccessCounted;

    protected Battle(final Long id,
                     final BattleName name,
                     final BattleIntroduction introduction,
                     final BattleImageUrl imageUrl,
                     final BattleBudget budget,
                     final BattleParticipantLimit battleParticipantLimit,
                     final BattleDuration duration,
                     final BattleStatus status,
                     final boolean isBattleSuccessCounted) {
        this.id = id;
        this.name = name;
        this.introduction = introduction;
        this.imageUrl = imageUrl;
        this.budget = budget;
        this.battleParticipantLimit = battleParticipantLimit;
        this.duration = duration;
        this.status = status;
        this.isBattleSuccessCounted = isBattleSuccessCounted;
    }

    public void startRecruit() {
        this.status = BattleStatus.RECRUITING;
    }

    public void finishRecruit() {
        this.status = BattleStatus.RECRUITING_FINISHED;
    }

    public void start() {
        this.status = BattleStatus.PROGRESS;
    }

    public void end() {
        this.status = BattleStatus.COMPLETE;
    }

    public boolean isRecruiting() {
        return hasSameStatus(BattleStatus.RECRUITING);
    }

    public boolean isRecruitingFinished() {
        return hasSameStatus(BattleStatus.RECRUITING_FINISHED);
    }

    public boolean isProgressing() {
        return hasSameStatus(BattleStatus.RECRUITING);
    }

    public boolean isFinished() {
        return hasSameStatus(BattleStatus.COMPLETE);
    }

    private boolean hasSameStatus(final BattleStatus status) {
        return this.status == status;
    }

    public long getNumberOfDaysBeforeEnd(final LocalDate current) {
        return ChronoUnit.DAYS.between(current, LocalDate.from(duration.getEnd()));
    }

    public long getNumberOfDaysAfterEnd(final LocalDate current) {
        return ChronoUnit.DAYS.between(LocalDate.from(duration.getEnd()), current);
    }

    public boolean isNumberOfParticipantAcceptable(final int participantSize) {
        return battleParticipantLimit.hasSameOrGreaterValue(participantSize);
    }

    public boolean isSuccess(final Long expenditure) {
        return budget.getValue() >= expenditure;
    }

    public Long getBudgetLeft(final Long expenditure) {
        return budget.getValue() - expenditure;
    }

    public BattleSizeType getBattleSizeType() {
        return battleParticipantLimit.getBattleSizeType();
    }

    public void successHistorySaved() {
        this.isBattleSuccessCounted = true;
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

    public Long getBudget() {
        return budget.getValue();
    }

    public int getBattleParticipantLimit() {
        return battleParticipantLimit.getValue();
    }

    public LocalDateTime getStart() {
        return duration.getStart();
    }

    public LocalDateTime getEnd() {
        return duration.getEnd();
    }

    public BattleStatus getStatus() {
        return status;
    }

    public String getDifficulty() {
        return budget.getDifficulty().name();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Battle battle = (Battle) o;
        return Objects.equals(id, battle.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
