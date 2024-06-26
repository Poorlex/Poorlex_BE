package com.poorlex.poorlex.battle.battle.domain;

import com.poorlex.poorlex.battle.battle.service.dto.request.BattleUpdateRequest;
import com.poorlex.poorlex.common.BaseCreatedAtEntity;
import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.exception.ExceptionTag;
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
import lombok.NonNull;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Battle extends BaseCreatedAtEntity {

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

    private boolean isBattleSuccessCounted = false;

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

    public void startWithoutValidate() {
        changeStatus(BattleStatus.PROGRESS);
    }

    public void start(final LocalDateTime dateTime) {
        validateStartable(dateTime);
        changeStatus(BattleStatus.PROGRESS);
    }

    private void validateStartable(final LocalDateTime startTime) {
        if (!isTimeAbleToStart(startTime) || !isStatusAbleToStart()) {
            final String errorMessage = String.format("배틀을 시작할 수 없습니다. ( 요청 : %s ( %d:%d ), 현재 배틀 상태 : %s)",
                                                      startTime.getDayOfWeek().name(),
                                                      startTime.getHour(),
                                                      startTime.getMinute(),
                                                      status.name()
            );
            throw new ApiException(ExceptionTag.BATTLE_PROGRESS, errorMessage);
        }
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

    public void endWithoutValidate() {
        changeStatus(BattleStatus.COMPLETE);
    }

    public void end(final LocalDateTime dateTime) {
        validateEndable(dateTime);
        changeStatus(BattleStatus.COMPLETE);
    }

    private void validateEndable(final LocalDateTime endTime) {
        if (!isTimeAbleToEnd(endTime) || !isStatusAbleToEnd()) {
            final String errorMessage = String.format("배틀을 종료할 수 없습니다. ( 요청 : %s ( %d:%d ), 현재 배틀 상태 : %s)",
                                                      endTime.getDayOfWeek().name(),
                                                      endTime.getHour(),
                                                      endTime.getMinute(),
                                                      status.name()
            );
            throw new ApiException(ExceptionTag.BATTLE_PROGRESS, errorMessage);
        }
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

    public boolean isRecruitingFinished() {
        return hasSameStatus(BattleStatus.RECRUITING_FINISHED);
    }

    public void recruit() {
        status = BattleStatus.RECRUITING;
    }

    public void finishRecruiting() {
        status = BattleStatus.RECRUITING_FINISHED;
    }

    public boolean hasSameMaxParticipantSize(final int targetSize) {
        return maxParticipantSize.hasSameValue(targetSize);
    }

    public boolean hasLessOrEqualMaxParticipantSizeThen(final int targetSize) {
        return !maxParticipantSize.hasGreaterValue(targetSize);
    }

    public void successPointSaved() {
        this.isBattleSuccessCounted = true;
    }

    public Long getDDay(final LocalDate current) {
        return ChronoUnit.DAYS.between(current, LocalDate.from(duration.getEnd()));
    }

    public Long getNumberOfDayPassedAfterStart(final LocalDate current) {
        return ChronoUnit.DAYS.between(LocalDate.from(duration.getStart()), current);
    }

    public Long getNumberOfDayPassedAfterEnd(final LocalDate current) {
        return ChronoUnit.DAYS.between(LocalDate.from(duration.getEnd()), current);
    }

    public boolean isSuccess(final Long expenditure) {
        return getBudgetLeft(expenditure) >= 0;
    }

    public Long getBudgetLeft(final Long expenditure) {
        return budget.getValue() - expenditure;
    }

    public int getBudgetLeft(final int expenditure) {
        return budget.getValue() - expenditure;
    }

    public BattleType getBattleType() {
        return maxParticipantSize.getBattleSizeType();
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

    public void update(BattleUpdateRequest request) {
        this.introduction = new BattleIntroduction(request.introduction());
        this.name = new BattleName(request.name());
    }

    public void updateImage(String imageUrl) {
        this.imageUrl = new BattleImageUrl(imageUrl);
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

    public boolean isBattlePointPaid() {
        return isBattleSuccessCounted;
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
