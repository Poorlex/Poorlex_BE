package com.poorlex.refactoring.battle.battle.domain;

import static com.poorlex.refactoring.battle.battle.domain.BattleDuration.BATTLE_DURATION_DAYS;
import static com.poorlex.refactoring.battle.battle.domain.BattleDuration.END_DAY_OF_WEEK;
import static com.poorlex.refactoring.battle.battle.domain.BattleDuration.END_HOUR;
import static com.poorlex.refactoring.battle.battle.domain.BattleDuration.END_MINUTE;
import static com.poorlex.refactoring.battle.battle.domain.BattleDuration.START_DAY_OF_WEEK;
import static com.poorlex.refactoring.battle.battle.domain.BattleDuration.START_HOUR;
import static com.poorlex.refactoring.battle.battle.domain.BattleDuration.START_MINUTE;

import com.poorlex.refactoring.battle.battle.domain.BattleCreateException.BattleBudgetException;
import com.poorlex.refactoring.battle.battle.domain.BattleCreateException.BattleDurationException;
import com.poorlex.refactoring.battle.battle.domain.BattleCreateException.BattleImageException;
import com.poorlex.refactoring.battle.battle.domain.BattleCreateException.BattleIntroductionException;
import com.poorlex.refactoring.battle.battle.domain.BattleCreateException.BattleNameException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Objects;
import org.springframework.util.StringUtils;

class BattleCreateValidator {

    private BattleCreateValidator() {

    }

    public static BattleName validBattleName(final String battleName) {
        BattleNameValidator.validate(battleName);
        return new BattleName(battleName);
    }

    public static BattleIntroduction validBattleIntroduction(final String introduction) {
        BattleIntroductionValidator.validate(introduction);
        return new BattleIntroduction(introduction);
    }

    public static BattleImageUrl validBattleImageUrl(final String imageUrl) {
        BattleImageUrlValidator.validate(imageUrl);
        return new BattleImageUrl(imageUrl);
    }

    public static BattleBudget validBattleBudget(final Long budget) {
        BattleBudgetValidator.validate(budget);
        return new BattleBudget(budget);
    }

    public static BattleParticipantLimit validParticipantSize(final int participantSize) {
        BattleParticipantSizeValidator.validate(participantSize);
        return new BattleParticipantLimit(participantSize);
    }

    public static BattleDuration validBattleDuration(final LocalDateTime start, final LocalDateTime end) {
        BattleDurationValidator.validate(start, end);
        return new BattleDuration(start, end);
    }

    private static class BattleNameValidator {

        private static final int MINIMUM_NAME_LENGTH = 2;
        private static final int MAXIMUM_NAME_LENGTH = 12;

        private static void validate(final String name) {
            validateNotEmpty(name);
            validateNameLength(name);
        }

        private static void validateNotEmpty(final String value) {
            if (!StringUtils.hasText(value)) {
                throw new BattleNameException("배틀명이 존재하지 않습니다.");
            }
        }

        private static void validateNameLength(final String name) {
            final int length = name.length();
            if (MINIMUM_NAME_LENGTH > length || length > MAXIMUM_NAME_LENGTH) {
                throw new BattleNameException(
                    String.format("배틀명은 길이는 %d ~ %d 사이여야 합니다.", MINIMUM_NAME_LENGTH, MAXIMUM_NAME_LENGTH)
                );
            }
        }
    }

    private static class BattleIntroductionValidator {

        private static final int MINIMUM_INTRODUCTION_LENGTH = 2;
        private static final int MAXIMUM_INTRODUCTION_LENGTH = 200;

        private static void validate(final String introduction) {
            validateNotEmpty(introduction);
            validateIntroductionLength(introduction);
        }

        private static void validateNotEmpty(final String value) {
            if (Objects.isNull(value) || !StringUtils.hasText(value)) {
                throw new BattleIntroductionException("배틀 소개가 존재하지 않습니다.");
            }
        }

        private static void validateIntroductionLength(final String introduction) {
            final int length = introduction.length();
            if (MINIMUM_INTRODUCTION_LENGTH > length || length > MAXIMUM_INTRODUCTION_LENGTH) {
                throw new BattleIntroductionException(
                    String.format(
                        "배틀 소개의 길이는 %d ~ %d 사이여야 합니다.",
                        MINIMUM_INTRODUCTION_LENGTH,
                        MAXIMUM_INTRODUCTION_LENGTH
                    )
                );
            }
        }
    }

    private static class BattleImageUrlValidator {

        private static void validate(final String imageUrl) {
            validateNotEmpty(imageUrl);
        }

        private static void validateNotEmpty(final String imageUrl) {
            if (Objects.isNull(imageUrl) || !StringUtils.hasText(imageUrl.strip())) {
                throw new BattleImageException("배틀 이미지 url이 존재하지 않습니다.");
            }
        }
    }

    private static class BattleBudgetValidator {

        private static final int BUDGET_UNIT = 10000;
        private static final int MINIMUM_BUDGET = 10000;
        private static final int MAXIMUM_BUDGET = 200000;

        private static void validate(final Long budget) {
            validateNotEmpty(budget);
            validateUnit(budget);
            validateRange(budget);
        }

        private static void validateNotEmpty(final Long budget) {
            if (Objects.isNull(budget)) {
                throw new BattleBudgetException("배틀 예산이 존재하지 않습니다.");
            }
        }

        private static void validateUnit(final Long budget) {
            if (budget % BUDGET_UNIT != 0) {
                throw new BattleBudgetException(
                    String.format("배틀 예산은 %d 단위여야 합니다.", BUDGET_UNIT)
                );
            }
        }

        private static void validateRange(final Long budget) {
            if (budget < MINIMUM_BUDGET || MAXIMUM_BUDGET < budget) {
                throw new BattleBudgetException(
                    String.format("배틀 예산의 범위는 %d ~ %d 까지 입니다.", MINIMUM_BUDGET, MAXIMUM_BUDGET)
                );
            }
        }
    }

    private static class BattleParticipantSizeValidator {

        private static final int MINIMUM_SIZE = 1;
        private static final int MAXIMUM_SIZE = 10;

        private static void validate(final int participantSize) {
            validateRange(participantSize);
        }

        private static void validateRange(final int participantSize) {
            if (participantSize < MINIMUM_SIZE || participantSize > MAXIMUM_SIZE) {
                throw new IllegalArgumentException();
            }
        }
    }

    private static class BattleDurationValidator {

        private static final Locale DAY_OF_WEEK_DISPLAY_LOCALE = Locale.KOREAN;
        private static final TextStyle DAY_OF_WEEK_DISPLAY_STYLE = TextStyle.SHORT;

        private static void validate(final LocalDateTime start, final LocalDateTime end) {
            validateNotNull(start, end);
            validateStart(start);
            validateEnd(end);
            validateDays(start, end);
        }

        private static void validateNotNull(final LocalDateTime start, final LocalDateTime end) {
            if (Objects.isNull(start)) {
                throw new BattleDurationException("배틀의 시작이 비어있습니다.");
            }
            if (Objects.isNull(end)) {
                throw new BattleDurationException("배틀의 종료가 비어있습니다.");
            }
        }

        private static void validateStart(final LocalDateTime start) {
            final DayOfWeek dayOfWeek = start.getDayOfWeek();
            final int hour = start.getHour();
            final int minute = start.getMinute();

            if (dayOfWeek != START_DAY_OF_WEEK || hour != START_HOUR || minute != START_MINUTE) {
                final String errorMessage = String.format("유효한 배틀 시작은 %s( %d : %d ) 입니다.",
                    START_DAY_OF_WEEK.getDisplayName(DAY_OF_WEEK_DISPLAY_STYLE, DAY_OF_WEEK_DISPLAY_LOCALE),
                    START_HOUR,
                    START_MINUTE
                );

                throw new BattleDurationException(errorMessage);
            }
        }

        private static void validateEnd(final LocalDateTime end) {
            final DayOfWeek dayOfWeek = end.getDayOfWeek();
            final int hour = end.getHour();
            final int minute = end.getMinute();

            if (dayOfWeek != END_DAY_OF_WEEK || hour != END_HOUR || minute != END_MINUTE) {
                final String exceptionMessage = String.format("유효한 배틀 종료는 %s( %d : %d ) 입니다.",
                    END_DAY_OF_WEEK.getDisplayName(DAY_OF_WEEK_DISPLAY_STYLE, DAY_OF_WEEK_DISPLAY_LOCALE),
                    END_HOUR,
                    END_MINUTE
                );

                throw new BattleDurationException(exceptionMessage);
            }
        }

        private static void validateDays(final LocalDateTime start, final LocalDateTime end) {
            if (ChronoUnit.DAYS.between(end, start) != BATTLE_DURATION_DAYS) {
                throw new BattleDurationException(
                    String.format("배틀의 기간은 %d일 이여야 합니다.", BATTLE_DURATION_DAYS)
                );
            }
        }
    }
}
