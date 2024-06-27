package com.poorlex.poorlex.consumption.weeklybudget.service;

import com.poorlex.poorlex.consumption.weeklybudget.domain.WeeklyBudget;
import com.poorlex.poorlex.consumption.weeklybudget.domain.WeeklyBudgetAmount;
import com.poorlex.poorlex.consumption.weeklybudget.domain.WeeklyBudgetRepository;
import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.exception.BadRequestException;
import com.poorlex.poorlex.exception.ExceptionTag;
import java.time.LocalDate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class WeeklyBudgetCommandService {

    private final WeeklyBudgetRepository weeklyBudgetRepository;

    public void createBudgetWithCurrentDuration(final Long memberId, final Long budget) {
        validateBudget(memberId);
        final WeeklyBudgetAmount amount = new WeeklyBudgetAmount(budget);
        final WeeklyBudget weeklyBudget = WeeklyBudget.withoutId(amount, memberId);

        weeklyBudgetRepository.save(weeklyBudget);
    }

    public void createBudgetWithDate(final Long memberId, final Long budget, final LocalDate date) {
        final WeeklyBudgetAmount amount = new WeeklyBudgetAmount(budget);
        final WeeklyBudget weeklyBudget = WeeklyBudget.withoutId(amount, memberId);

        weeklyBudgetRepository.save(weeklyBudget);
    }

    public void updateBudget(Long memberId, Long budget) {
        WeeklyBudgetAmount amount = new WeeklyBudgetAmount(budget);
        WeeklyBudget weeklyBudget = weeklyBudgetRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(ExceptionTag.MEMBER_FIND, "해당 멤버를 찾을 수 없습니다."));
        weeklyBudget.updateAmount(amount);

        weeklyBudgetRepository.save(weeklyBudget);
    }

    private void validateBudget(Long memberId) {
        if (weeklyBudgetRepository.findByMemberId(memberId).isPresent()) {
            throw new ApiException(ExceptionTag.WEEKLY_BUDGET_STATUS, "주간 예산이 이미 존재합니다.");
        }
    }

    public void deleteBudget(Long memberId) {
        weeklyBudgetRepository.findByMemberId(memberId)
                .ifPresent(weeklyBudgetRepository::delete);
    }
}
