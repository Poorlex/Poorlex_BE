package com.poolex.poolex.weeklybudget.service;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.poolex.poolex.member.domain.Member;
import com.poolex.poolex.member.domain.MemberNickname;
import com.poolex.poolex.member.domain.MemberRepository;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import com.poolex.poolex.support.UsingDataJpaTest;
import com.poolex.poolex.weeklybudget.domain.WeeklyBudget;
import com.poolex.poolex.weeklybudget.domain.WeeklyBudgetDuration;
import com.poolex.poolex.weeklybudget.domain.WeeklyBudgetRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class WeeklyBudgetServiceTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private WeeklyBudgetRepository weeklyBudgetRepository;

    private WeeklyBudgetService weeklyBudgetService;

    @BeforeEach
    void setUp() {
        this.weeklyBudgetService = new WeeklyBudgetService(weeklyBudgetRepository, memberRepository);
    }

    @Test
    void 주간_예산을_생성한다() {
        //given
        final Member member = memberRepository.save(Member.withoutId(new MemberNickname("nickname")));

        //when
        weeklyBudgetService.createBudget(member.getId(), 10000);

        //then
        final List<WeeklyBudget> weeklyBudgets = weeklyBudgetRepository.findAll();
        assertSoftly(
            softly -> {
                softly.assertThat(weeklyBudgets).hasSize(1);
                final WeeklyBudget weeklyBudget = weeklyBudgets.get(0);
                softly.assertThat(weeklyBudget.getAmount()).isEqualTo(10000);
                softly.assertThat(weeklyBudget.getDuration())
                    .usingRecursiveComparison()
                    .isEqualTo(WeeklyBudgetDuration.current());
                softly.assertThat(weeklyBudget.getMemberId()).isEqualTo(member.getId());
            }
        );
    }
}
