package com.poorlex.poorlex.consumption.weeklybudget.service;

import com.poorlex.poorlex.bridge.MemberExistenceProviderImpl;
import com.poorlex.poorlex.consumption.weeklybudget.domain.WeeklyBudget;
import com.poorlex.poorlex.consumption.weeklybudget.domain.WeeklyBudgetDuration;
import com.poorlex.poorlex.consumption.weeklybudget.domain.WeeklyBudgetRepository;
import com.poorlex.poorlex.consumption.weeklybudget.service.provider.MemberExistenceProvider;
import com.poorlex.poorlex.fixture.MemberFixture;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.db.UsingDataJpaTest;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId;
import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class WeeklyBudgetCommandServiceTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private WeeklyBudgetRepository weeklyBudgetRepository;

    private MemberExistenceProvider memberExistenceProvider;

    private WeeklyBudgetCommandService weeklyBudgetCommandService;

    @BeforeEach
    void setUp() {
        memberExistenceProvider = new MemberExistenceProviderImpl(memberRepository);
        this.weeklyBudgetCommandService = new WeeklyBudgetCommandService(weeklyBudgetRepository,
                                                                         memberExistenceProvider);
    }

    @Test
    void 주간_예산을_생성한다() {
        //given
        final Member 회원 = MemberFixture.saveMemberWithOauthId(memberRepository,
                                                              Oauth2RegistrationId.APPLE,
                                                              "oauthId",
                                                              "스플릿",
                                                              "소개");
        final Long 주간_에산_금액 = 10000L;
        final LocalDate 현재_날짜 = LocalDate.now();

        //when
        weeklyBudgetCommandService.createBudgetWithDate(회원.getId(), 주간_에산_금액, 현재_날짜);

        //then
        final List<WeeklyBudget> 주간_예산_전체_목록 = weeklyBudgetRepository.findAll();

        assertSoftly(
                softly -> {
                    softly.assertThat(주간_예산_전체_목록).hasSize(1);
                    final WeeklyBudget weeklyBudget = 주간_예산_전체_목록.get(0);
                    softly.assertThat(weeklyBudget.getAmount()).isEqualTo(주간_에산_금액);
                    softly.assertThat(weeklyBudget.getDuration())
                            .usingRecursiveComparison()
                            .isEqualTo(WeeklyBudgetDuration.from(현재_날짜));
                    softly.assertThat(weeklyBudget.getMemberId()).isEqualTo(회원.getId());
                }
        );
    }
}
