package com.poorlex.poorlex.weeklybudget.service;

import com.poorlex.poorlex.expenditure.domain.Expenditure;
import com.poorlex.poorlex.expenditure.domain.ExpenditureRepository;
import com.poorlex.poorlex.expenditure.fixture.ExpenditureFixture;
import com.poorlex.poorlex.member.domain.Member;
import com.poorlex.poorlex.member.domain.MemberNickname;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.member.domain.Oauth2RegistrationId;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.db.UsingDataJpaTest;
import com.poorlex.poorlex.weeklybudget.domain.WeeklyBudget;
import com.poorlex.poorlex.weeklybudget.domain.WeeklyBudgetAmount;
import com.poorlex.poorlex.weeklybudget.domain.WeeklyBudgetDuration;
import com.poorlex.poorlex.weeklybudget.domain.WeeklyBudgetRepository;
import com.poorlex.poorlex.weeklybudget.service.dto.response.WeeklyBudgetLeftResponse;
import com.poorlex.poorlex.weeklybudget.service.dto.response.WeeklyBudgetResponse;
import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class WeeklyBudgetServiceTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private WeeklyBudgetRepository weeklyBudgetRepository;

    @Autowired
    private ExpenditureRepository expenditureRepository;

    private WeeklyBudgetService weeklyBudgetService;

    @BeforeEach
    void setUp() {
        this.weeklyBudgetService =
                new WeeklyBudgetService(weeklyBudgetRepository, expenditureRepository, memberRepository);
    }

    @Test
    void 주간_예산을_생성한다() {
        //given
        final Member member = memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));

        //when
        weeklyBudgetService.createBudget(member.getId(), 10000L);

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

    @Test
    void 멤버의_현재날짜_기준_주간_예산을_조회한다_존재할때() {
        //given
        final Member member = memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        final WeeklyBudget weaklyBudget = createWeaklyBudget(member.getId(), 10000L);

        //when
        final WeeklyBudgetResponse weeklyBudgetResponse = weeklyBudgetService.findCurrentBudgetByMemberIdAndDate(
                member.getId(),
                weaklyBudget.getDuration().getStart()
        );

        //then
        assertThat(weeklyBudgetResponse.isExist()).isTrue();
        assertThat(weeklyBudgetResponse.getAmount()).isEqualTo(weaklyBudget.getAmount());
    }

    @Test
    void 멤버의_현재날짜_기준_주간_예산을_조회한다_존재하지_않을_때() {
        //given
        final Member member = memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));

        //when
        final WeeklyBudgetResponse weeklyBudgetResponse = weeklyBudgetService.findCurrentBudgetByMemberIdAndDate(
                member.getId(),
                LocalDate.now()
        );

        //then
        assertThat(weeklyBudgetResponse.isExist()).isFalse();
        assertThat(weeklyBudgetResponse.getAmount()).isZero();
    }

    @Test
    void 멤버의_현재날짜_기준_남은_주간_예산을_조회한다() {
        //given
        final Member member = memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        final WeeklyBudget weaklyBudget = createWeaklyBudget(member.getId(), 10000L);
        final Expenditure expenditure = expend(1000,
                                               member.getId(),
                                               LocalDate.from(weaklyBudget.getDuration().getStart()));

        //when
        final WeeklyBudgetLeftResponse budgetLeft = weeklyBudgetService.findCurrentBudgetLeftByMemberIdAndDate(
                member.getId(),
                weaklyBudget.getDuration().getStart()
        );

        //then
        assertThat(budgetLeft.isExist()).isTrue();
        assertThat(budgetLeft.getAmount()).isEqualTo(weaklyBudget.getAmount() - expenditure.getAmount());
    }

    @Test
    void 멤버의_현재날짜_기준_남은_주간_예산을_조회한다_등록된_주간_예산이_없을때() {
        //given
        final Member member = memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        final LocalDate date = LocalDate.now();
        expend(1000, member.getId(), date);

        //when
        final WeeklyBudgetLeftResponse budgetLeft = weeklyBudgetService.findCurrentBudgetLeftByMemberIdAndDate(member.getId(),
                                                                                                               date);

        //then
        assertThat(budgetLeft.isExist()).isFalse();
        assertThat(budgetLeft.getAmount()).isZero();
    }

    private WeeklyBudget createWeaklyBudget(final Long memberId, final Long amount) {
        final WeeklyBudget weeklyBudget = WeeklyBudget.withoutId(
                new WeeklyBudgetAmount(amount),
                WeeklyBudgetDuration.current(),
                memberId
        );

        return weeklyBudgetRepository.save(weeklyBudget);
    }

    private Expenditure expend(final int amount, final Long memberId, final LocalDate date) {
        return expenditureRepository.save(ExpenditureFixture.simpleWithMainImage(amount, memberId, date));
    }
}
